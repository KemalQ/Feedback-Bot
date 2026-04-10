package com.feedbackbot.service.impl;


import com.feedbackbot.enums.UserRole;
import com.feedbackbot.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardFactory {

    private final ProducerService producerService;

    public KeyboardFactory(ProducerService producerService) {
        this.producerService = producerService;
    }

    public void sendRoleKeyboard(Long chatId, String branch) {
        SendMessage message = SendMessage.builder().
                chatId(chatId)
                .text("Branch: *" + branch + "*\n\nSelect your position:")
                .parseMode("Markdown")
                .replyMarkup(buildRoleKeyboard()).build();
        producerService.produceAnswer(message);
    }

    private InlineKeyboardMarkup buildRoleKeyboard() {
        // 2 column, 5 lines + 1 line with 1 button
        List<InlineKeyboardRow> rows = new ArrayList<>();

        String[][] roleGrid = {
                {"MASTER_RECEIVER", "Master Receiver",  "MECHANIC",        "Mechanic"},
                {"ELECTRICIAN",     "Auto Electrician", "PAINTER",         "Auto Painter"},
                {"PANEL_BEATER",    "Panel Beater",     "DIAGNOSTICIAN",   "Diagnostician"},
                {"COLORIST",        "Colorist",         "TRIMMER",         "Trimmer"},
                {"TIRE_TECHNICIAN", "Tire Technician",  "CAR_WASHER",      "Car Washer"}
        };

        for (String[] row : roleGrid) {
            List<InlineKeyboardButton> rowButtons = new ArrayList<>();
            // every line = 2 buttons
            for (int i = 0; i < row.length; i += 2) {
                InlineKeyboardButton btn = InlineKeyboardButton.builder()
                        .text(row[i + 1])
                        .callbackData("ROLE_" + row[i])
                        .build();
                rowButtons.add(btn);
            }
            rows.add(new InlineKeyboardRow(rowButtons));
        }

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    public String formatRole(UserRole role) {
        return switch (role) {
            case MASTER_RECEIVER  -> "Master Receiver";
            case MECHANIC         -> "Mechanic";
            case ELECTRICIAN      -> "Auto Electrician";
            case PAINTER          -> "Auto Painter";
            case PANEL_BEATER     -> "Panel Beater";
            case DIAGNOSTICIAN    -> "Diagnostician";
            case COLORIST         -> "Colorist";
            case TRIMMER          -> "Trimmer";
            case TIRE_TECHNICIAN  -> "Tire Technician";
            case CAR_WASHER       -> "Car Washer";
            case MANAGER          -> "Manager";
        };
    }
}
