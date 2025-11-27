package com.example.demo.chat.controller;

import com.example.demo.chat.dto.*;
import com.example.demo.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // ============================================
    // 1) 채팅방 목록 조회
    // ============================================
    @Operation(
            summary = "채팅방 목록 조회",
            description = "사용자가 생성한 모든 채팅방 목록을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/rooms")
    public List<ChatRoomSummaryDto> getRooms(@AuthenticationPrincipal Long userId) {
        return chatService.getChatRooms(userId);
    }

    // ============================================
    // 2) 새 채팅 시작
    // ============================================
    @Operation(
            summary = "새 채팅 시작",
            description = "첫 메시지를 기반으로 새로운 채팅방을 생성하고, AI의 첫 응답까지 포함해 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 오류")
    })
    @PostMapping("/rooms")
    public ChatResponseDto startNewChat(
            @AuthenticationPrincipal Long userId,
            @RequestBody NewChatRequest req
    ) {
        return chatService.startNewChat(userId, req);
    }

    // ============================================
    // 3) 기존 채팅방 이어쓰기 (메시지 보내기)
    // ============================================
    @Operation(
            summary = "기존 채팅에서 메시지 전송",
            description = "사용자가 메시지를 보내면 AI가 답변하고 전체 대화 내용을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전송 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방 없음")
    })
    @PostMapping("/rooms/{roomId}/messages")
    public ChatResponseDto sendMessage(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId,
            @RequestBody SendMessageRequest req
    ) {
        return chatService.sendMessage(userId, roomId, req);
    }

    // ============================================
    // 4) 특정 채팅방 상세 조회
    // ============================================
    @Operation(
            summary = "채팅방 상세 조회",
            description = "채팅방의 전체 메시지 히스토리를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "채팅방 없음")
    })
    @GetMapping("/rooms/{roomId}")
    public ChatResponseDto getChatRoomDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long roomId
    ) {
        return chatService.getChatRoomDetail(userId, roomId);
    }
}
