package com.example.demo.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatResponseDto {

    private Long roomId;
    private String roomTitle;

    private List<ChatMessageDto> messages;

    private String lastAiMessage;
}
