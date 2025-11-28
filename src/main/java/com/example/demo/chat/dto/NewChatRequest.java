package com.example.demo.chat.dto;

import com.example.demo.chat.entity.CharacterType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewChatRequest {

    private String message;
    private String title;
    private CharacterType character;  // ⭐ ENUM 적용
}
