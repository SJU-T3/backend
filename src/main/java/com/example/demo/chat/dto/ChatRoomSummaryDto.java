package com.example.demo.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomSummaryDto {
    private Long roomId;
    private String title;
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
}
