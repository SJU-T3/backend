package com.example.demo.chat.controller;

import com.example.demo.chat.dto.ChatResponseDto;
import com.example.demo.chat.dto.ChatRoomSummaryDto;
import com.example.demo.chat.dto.NewChatRequest;
import com.example.demo.chat.dto.SendMessageRequest;
import com.example.demo.chat.entity.CharacterType;
import com.example.demo.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/rooms")
    public List<ChatRoomSummaryDto> getRooms(@AuthenticationPrincipal Long userId) {
        System.out.println(">>> ChatController userId = " + userId);
        return chatService.getChatRooms(userId);
    }

    @PostMapping("/rooms")
    public ChatResponseDto startNewChat(
            @AuthenticationPrincipal Long userId,
            @RequestBody NewChatRequest req
    ) {
        return chatService.startNewChat(userId, req);
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ChatResponseDto sendMessage(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId,
            @RequestBody SendMessageRequest req
    ) {
        return chatService.sendMessage(userId, roomId, req);
    }

    @GetMapping("/rooms/{roomId}")
    public ChatResponseDto getChatRoomDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId
    ) {
        return chatService.getChatRoomDetail(userId, roomId);
    }

    // ============================================
    // ⭐ 캐릭터 변경 ENUM 적용
    // ============================================
    @PatchMapping("/rooms/{roomId}/character")
    public ChatRoomSummaryDto updateCharacter(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId,
            @RequestBody Map<String, String> body
    ) {
        CharacterType character = CharacterType.valueOf(body.get("character"));
        return chatService.updateCharacter(userId, roomId, character);
    }
}
