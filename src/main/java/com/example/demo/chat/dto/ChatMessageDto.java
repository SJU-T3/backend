package com.example.demo.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageDto {
    private Long id;
    private String role;
    private String content;
    private LocalDateTime createdAt;
}
