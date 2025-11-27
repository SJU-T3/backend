package com.example.demo.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewChatRequest {
    private String message;
    private String title;
}
