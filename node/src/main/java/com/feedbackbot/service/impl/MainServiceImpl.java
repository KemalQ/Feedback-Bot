package com.feedbackbot.service.impl;

import com.feedbackbot.dao.AppUserDAO;
import com.feedbackbot.dao.FeedbackMessageDAO;
import com.feedbackbot.dao.InviteTokenDAO;
import com.feedbackbot.dao.RawDataDAO;
import com.feedbackbot.entity.AppUser;
import com.feedbackbot.entity.InviteToken;
import com.feedbackbot.entity.RawData;
import com.feedbackbot.enums.ServiceCommand;
import com.feedbackbot.enums.UserRole;
import com.feedbackbot.integrations.ai.SpringAIAnalysisService;
import com.feedbackbot.integrations.sheets.GoogleSheetsService;
import com.feedbackbot.integrations.trello.TrelloService;
import com.feedbackbot.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.feedbackbot.enums.ServiceCommand.CANCEL;
import static com.feedbackbot.enums.ServiceCommand.START;
import static com.feedbackbot.enums.UserState.*;

@Slf4j
@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final InviteTokenDAO inviteTokenDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final SpringAIAnalysisService springAIAnalysisService;
    private final FeedbackMessageDAO feedbackMessageDAO;
    private final FeedbackProcessingServiceImpl feedbackProcessingService;
    private final KeyboardFactory keyboardFactory;

    public MainServiceImpl(RawDataDAO rawDataDAO,
                           InviteTokenDAO inviteTokenDAO,
                           ProducerService producerService, AppUserDAO appUserDAO,
                           SpringAIAnalysisService springAIAnalysisService,
                           FeedbackMessageDAO feedbackMessageDAO,
                           FeedbackProcessingServiceImpl feedbackProcessingService, KeyboardFactory keyboardFactory) {
        this.rawDataDAO = rawDataDAO;
        this.inviteTokenDAO = inviteTokenDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.springAIAnalysisService = springAIAnalysisService;
        this.feedbackMessageDAO = feedbackMessageDAO;
        this.feedbackProcessingService = feedbackProcessingService;
        this.keyboardFactory = keyboardFactory;
    }


    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var output = "";

        var serviceCommand = ServiceCommand.fromValue(text);

        // /cancel works from any state
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
            sendAnswer(output, chatId);
            return;
        }

        // FSM
        switch (userState) {
            case NEW -> output = handleNewState(appUser, text);
            case CHOOSING_ROLE -> output = handleChoosingRole(appUser, text, chatId);
            case ACTIVE -> output = handleActiveFeedback(appUser, text);
            default -> {
                log.error("Unknown user state: {}", userState);
                output = "Something went wrong. Type /cancel and try again.";
            }
        }

        sendAnswer(output, chatId);
    }

    /// NEW: waiting /start without token
    private String handleNewState(AppUser appUser, String text) {
        var serviceCommand = ServiceCommand.fromValue(text);

        if (!START.equals(serviceCommand)) {
            return "Please use the invite link to get started.";
        }

        ///start without token — base /start
        return "Welcome! Please use the invite link provided by your manager to register.";
    }

    /// method for /start with token (called from processStartWithToken)
    @Override
    public void processStartWithToken(Update update, String token) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        // if registered
        if (ACTIVE.equals(appUser.getState())) {
            sendAnswer("You are already registered. Send your feedback anytime.", chatId);
            return;
        }

        // Checking the token
        Optional<InviteToken> inviteOpt = inviteTokenDAO.findByTokenAndIsActiveTrue(token);

        if (inviteOpt.isEmpty()) {
            log.debug("TOKEN FROM USER: [{}]", token);
            sendAnswer("This invite link is invalid or has expired. Contact your manager.", chatId);
            return;
        }

        InviteToken inviteToken = inviteOpt.get();

        // Checking the expiration date
        if (inviteToken.getExpiresAt() != null &&
                inviteToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            sendAnswer("This invite link has expired. Contact your manager.", chatId);
            return;
        }

        // Setting branch from token and transferring to CHOOSING_ROLE
        appUser.setBranch(inviteToken.getBranch());
        appUser.setState(CHOOSING_ROLE);
        appUserDAO.save(appUser);

        // Sending inline keyboard for choosing
        keyboardFactory.sendRoleKeyboard(chatId, inviteToken.getBranch());
    }

    /// CHOOSING_ROLE: The user clicked the role button
    private String handleChoosingRole(AppUser appUser, String text, Long chatId) {
        // text = callback data from InlineKeyboard, example- "ROLE_MECHANIC"
        if (!text.startsWith("ROLE_")) {
            return "Please select your position using the buttons below.";
        }

        String roleStr = text.replace("ROLE_", "");
        try {
            UserRole role = UserRole.valueOf(roleStr);
            appUser.setRole(role);
            appUser.setState(ACTIVE);
            appUserDAO.save(appUser);

            log.info("✅ User {} registered: branch={}, role={}",
                    appUser.getTelegramUserId(), appUser.getBranch(), role);

            return String.format(
                    "✅ Registration complete!\n\nBranch: %s\nPosition: %s\n\n" +
                            "You can now send your feedback anonymously. " +
                            "Your name is never stored — only your position and branch.",
                    appUser.getBranch(),
                    keyboardFactory.formatRole(role)
            );
        } catch (IllegalArgumentException e) {
            log.error("Unknown role received: {}", roleStr);
            return "Unknown position. Please use the buttons to select.";
        }
    }

    /// ACTIVE: accept feedback
    private String handleActiveFeedback(AppUser appUser, String text) {
        if (text == null || text.isBlank()) {
            return "Please send a text message with your feedback.";
        }
        if (text.length() < 5) {
            return "Feedback is too short. Please describe the issue in more detail.";
        }
        if (text.length() > 2000) {
            return "Feedback is too long (max 2000 characters). Please shorten it.";
        }

        log.info("Feedback received from user {}: {} chars", appUser.getTelegramUserId(), text.length());

        // response to the user
        return feedbackProcessingService.process(text, appUser);

    }

    //

    private String help(){
        return "List of available commands:\n"
                + "/cancel - cancel execution of the current command\n"
                + "/registration - user registration";
    }

    private String cancelProcess(AppUser appUser) {
        if (ACTIVE.equals(appUser.getState())) {
            return "Nothing to cancel. Send your feedback anytime.";
        }
        appUser.setState(NEW);
        appUserDAO.save(appUser);
        return "Cancelled. Use your invite link to start again.";
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser = update.getMessage().getFrom();
        return appUserDAO.findByTelegramUserId(telegramUser.getId())
                .orElseGet(()->{
                    AppUser newUser = AppUser.builder()
                            .telegramUserId(telegramUser.getId())
                            .userName(telegramUser.getUserName())
                            .firstName(telegramUser.getFirstName())
                            .lastName(telegramUser.getLastName())
                            .state(NEW)
                            .build();
                    return appUserDAO.save(newUser);
                });
    }

    private void sendAnswer(String text, Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        producerService.produceAnswer(message);
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder().event(update).build();
        rawDataDAO.save(rawData);//storing in DB and setting id
    }

    @Override
    public void processCallback(Update update, String callbackData) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramUserId = update.getCallbackQuery().getFrom().getId();

        AppUser appUser = appUserDAO.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new IllegalStateException("User not found for callback"));

        if (callbackData.startsWith("ROLE_") && CHOOSING_ROLE.equals(appUser.getState())) {
            String result = handleChoosingRole(appUser, callbackData, chatId);
            sendAnswer(result, chatId);
        } else {
            sendAnswer("Please use the invite link to start.", chatId);
        }
    }
}
