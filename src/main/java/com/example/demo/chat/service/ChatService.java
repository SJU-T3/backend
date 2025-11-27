package com.example.demo.chat.service;

import com.example.demo.calendar.service.ReportService;
import com.example.demo.calendar.ai.AiClient;
import com.example.demo.chat.dto.*;
import com.example.demo.chat.entity.ChatMessage;
import com.example.demo.chat.entity.ChatRoom;
import com.example.demo.chat.repository.ChatMessageRepository;
import com.example.demo.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final AiClient aiClient;
    private final ReportService reportService;   // 소비리포트 사용 위해 추가

    // ==========================
    // 자동 제목 생성
    // ==========================
    private String makeAutoTitle(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) return "소비 상담";
        String t = userMessage.trim();
        return t.length() > 15 ? t.substring(0, 15) : t;
    }

    // ==========================
    // 채팅방 목록 조회
    // ==========================
    @Transactional(readOnly = true)
    public List<ChatRoomSummaryDto> getChatRooms(Long userId) {
        return roomRepo.findByUserIdOrderByLastMessageAtDesc(userId)
                .stream()
                .map(room -> ChatRoomSummaryDto.builder()
                        .roomId(room.getId())
                        .title(room.getTitle())
                        .lastMessagePreview(room.getLastMessagePreview())
                        .lastMessageAt(room.getLastMessageAt())
                        .build())
                .collect(toList());
    }

    // ==========================
    // 채팅방 상세 조회
    // ==========================
    @Transactional(readOnly = true)
    public ChatResponseDto getChatRoomDetail(Long userId, Long roomId) {
        ChatRoom room = roomRepo.findByIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));

        List<ChatMessage> messages = msgRepo.findByChatRoomOrderByCreatedAtAsc(room);

        List<ChatMessageDto> dtoList = messages.stream()
                .map(this::toDto)
                .collect(toList());

        return ChatResponseDto.builder()
                .roomId(room.getId())
                .roomTitle(room.getTitle())
                .messages(dtoList)
                .lastAiMessage(getLastAiMessage(messages))
                .build();
    }

    // ==========================
    // 새 채팅 시작
    // ==========================
    public ChatResponseDto startNewChat(Long userId, NewChatRequest request) {

        // 제목 자동 생성
        String title = (request.getTitle() != null && !request.getTitle().isBlank())
                ? request.getTitle()
                : makeAutoTitle(request.getMessage());

        // 채팅방 저장
        ChatRoom room = ChatRoom.builder()
                .userId(userId)
                .title(title)
                .lastMessagePreview(request.getMessage())
                .lastMessageAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        roomRepo.save(room);

        // 사용자 메시지 저장
        ChatMessage userMsg = saveMessage(room, ChatMessage.Role.USER, request.getMessage());

        // 🔥ChatService에서 소비리포트 불러오는 자리
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        // 🔥 소비리포트 JSON 전체 가져오기
        String spendingReport = reportService.getReportJson(userId, year, month);

        // 🔥 프롬프트 생성 (소비리포트 + 사용자의 첫 메시지)
        String prompt = buildPrompt(spendingReport, room, List.of(userMsg));

        // AI 호출
        String aiReply;
        try {
            aiReply = aiClient.invoke(prompt);
        } catch (Exception e) {
            throw new RuntimeException("AI 호출 실패", e);
        }

        // AI 메시지 저장
        ChatMessage aiMsg = saveMessage(room, ChatMessage.Role.ASSISTANT, aiReply);

        room.setLastMessagePreview(aiReply);
        room.setLastMessageAt(aiMsg.getCreatedAt());

        return ChatResponseDto.builder()
                .roomId(room.getId())
                .roomTitle(room.getTitle())
                .messages(List.of(toDto(userMsg), toDto(aiMsg)))
                .lastAiMessage(aiReply)
                .build();
    }

    // ==========================
    // 기존 채팅 이어하기
    // ==========================
    public ChatResponseDto sendMessage(Long userId, Long roomId, SendMessageRequest request) {

        ChatRoom room = roomRepo.findByIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));

        // 사용자 메시지 저장
        ChatMessage userMsg = saveMessage(room, ChatMessage.Role.USER, request.getMessage());

        // 대화 히스토리
        List<ChatMessage> history = msgRepo.findByChatRoomOrderByCreatedAtAsc(room);

        // 🔥 ChatService에서 소비리포트 불러오는 자리
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        // 🔥 소비리포트 JSON 가져오기
        String spendingReport = reportService.getReportJson(userId, year, month);

        // 🔥 프롬프트 생성
        String prompt = buildPrompt(spendingReport, room, history);

        // AI 호출
        String aiReply;
        try {
            aiReply = aiClient.invoke(prompt);
        } catch (Exception e) {
            throw new RuntimeException("AI 호출 실패", e);
        }

        ChatMessage aiMsg = saveMessage(room, ChatMessage.Role.ASSISTANT, aiReply);

        room.setLastMessagePreview(aiReply);
        room.setLastMessageAt(aiMsg.getCreatedAt());

        List<ChatMessageDto> allMessages = msgRepo.findByChatRoomOrderByCreatedAtAsc(room)
                .stream().map(this::toDto)
                .collect(toList());

        return ChatResponseDto.builder()
                .roomId(room.getId())
                .roomTitle(room.getTitle())
                .messages(allMessages)
                .lastAiMessage(aiReply)
                .build();
    }

    // ==========================
    // 공통 메서드
    // ==========================
    private ChatMessage saveMessage(ChatRoom room, ChatMessage.Role role, String content) {
        ChatMessage msg = ChatMessage.builder()
                .chatRoom(room)
                .role(role)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        return msgRepo.save(msg);
    }

    private ChatMessageDto toDto(ChatMessage m) {
        return ChatMessageDto.builder()
                .id(m.getId())
                .role(m.getRole().name().toLowerCase())
                .content(m.getContent())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private String getLastAiMessage(List<ChatMessage> messages) {
        return messages.stream()
                .filter(m -> m.getRole() == ChatMessage.Role.ASSISTANT)
                .reduce((a, b) -> b)
                .map(ChatMessage::getContent)
                .orElse(null);
    }

    // ==========================
    // 프롬프트 생성
    // ==========================
    private String buildPrompt(String spendingJson,
                               ChatRoom room,
                               List<ChatMessage> history) {

        StringBuilder conv = new StringBuilder();
        for (ChatMessage m : history) {
            conv.append(m.getRole() == ChatMessage.Role.USER ? "사용자: " : "AI: ")
                    .append(m.getContent()).append("\n");
        }

        return """
            당신은 소비습관을 분석하는 재무 코치 AI입니다.
            모든 답변은 공손한 한국어 존댓말로 작성해 주세요.
            사용자를 비난하지 말고 실천 가능한 조언 중심으로 답해주세요.

            [사용자의 소비 리포트 JSON]
            %s

            [채팅방 제목]
            %s

            [이전 대화]
            %s

            위 대화를 계속 자연스럽게 이어서 답변해 주세요.
            """.formatted(
                spendingJson,
                room.getTitle(),
                conv.toString()
        );
    }
}
