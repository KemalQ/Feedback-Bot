package com.feedbackbot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoiceUpdateDto implements Serializable {
    private Update update;
    private byte[] voiceBytes;
    private Integer durationSeconds;
    private String mimeType;
}
