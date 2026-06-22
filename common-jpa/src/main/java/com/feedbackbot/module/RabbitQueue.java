package com.feedbackbot.module;

public class RabbitQueue {
    ///     EXCHANGE
    public static final String DIRECT_EXCHANGE = "direct.exchange";

    ///     ROUTE

    public static final String TEXT_ROUTE = "text_route";
    public static final String ANSWER_ROUTE = "answer_route";
    public static final String VOICE_ROUTE = "audio_route";


    ///     QUEUE
    public static final String TEXT_MESSAGE_UPDATE = "text_message_update";
    public static final String ANSWER_MESSAGE = "answer_message";
    public static final String VOICE_ROUTE_UPDATE = "audio_route_update";
}