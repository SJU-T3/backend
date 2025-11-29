package com.example.demo.chat.dto;

import com.example.demo.chat.entity.CharacterType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomSummaryDto {

    private Long roomId;
    private String title;
    private String lastMessagePreview;
    private CharacterType character;  // ⭐ ENUM 적용
}
