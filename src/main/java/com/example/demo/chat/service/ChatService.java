package com.example.demo.chat.service;

import com.example.demo.chat.dto.ChatResponseDto;
import com.example.demo.chat.dto.ChatRoomSummaryDto;
import com.example.demo.chat.dto.NewChatRequest;
import com.example.demo.chat.dto.SendMessageRequest;
import com.example.demo.chat.entity.ChatMessage;
import com.example.demo.chat.entity.ChatRoom;
import com.example.demo.chat.entity.CharacterType;
import com.example.demo.chat.repository.ChatMessageRepository;
import com.example.demo.chat.repository.ChatRoomRepository;
import com.example.demo.chat.ai.ChatAiClient;
import com.example.demo.chat.dto.ChatMessageDto;
import com.example.demo.calendar.entity.Goal;
import com.example.demo.calendar.entity.Transaction;
import com.example.demo.calendar.repository.TransactionRepository;
import com.example.demo.calendar.service.GoalService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatAiClient chatAiClient;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final GoalService goalService;
    private final TransactionRepository transactionRepository;
    // ======================================================
    // 1) 채팅방 목록 조회
    // ======================================================
    public List<ChatRoomSummaryDto> getChatRooms(Long userId) {

        List<ChatRoom> rooms =
                chatRoomRepository.findByUserIdOrderByLastMessageAtDesc(userId);

        return rooms.stream()
                .map(room -> new ChatRoomSummaryDto(
                        room.getId(),
                        room.getTitle(),
                        room.getLastMessagePreview(),
                        room.getCharacter()
                ))
                .collect(Collectors.toList());
    }

    // ======================================================
    // 2) 새 채팅 시작
    // ======================================================
    @Transactional
    public ChatResponseDto startNewChat(Long userId, NewChatRequest req) {

        ChatRoom room = ChatRoom.builder()
                .userId(userId)
                .title(req.getTitle())
                .lastMessagePreview(req.getMessage())
                .lastMessageAt(LocalDateTime.now())
                .character(req.getCharacter())         // ⭐ ENUM 저장
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(room);

        // 최초 사용자 메시지 저장
        ChatMessage userMessage = ChatMessage.builder()
                .chatRoom(room)
                .role(ChatMessage.Role.USER)
                .content(req.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(userMessage);
        String aiReply;
        try {
            String prompt = buildPrompt(userId, req.getMessage());
            aiReply = chatAiClient.invoke(prompt);
        } catch (Exception e) {
            aiReply = "안녕하세요! 무엇을 도와드릴까요?";
        }

        ChatMessage aiMessage = ChatMessage.builder()
                .chatRoom(room)
                .role(ChatMessage.Role.ASSISTANT)
                .content(aiReply)
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(aiMessage);

        room.setLastMessagePreview(aiReply);

        // AI 응답 생성 등 기존 로직 그대로
        return createResponseDto(room.getId());
    }

    // ======================================================
    // 3) 기존 채팅 메시지 전송
    // ======================================================
    @Transactional
    public ChatResponseDto sendMessage(Long userId, Long roomId, SendMessageRequest req) {

        ChatRoom room = chatRoomRepository.findByIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 사용자 메시지 저장
        ChatMessage userMessage = ChatMessage.builder()
                .chatRoom(room)
                .role(ChatMessage.Role.USER)
                .content(req.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(userMessage);
        String aiReply;
        try {
            String prompt = buildPrompt(userId, req.getMessage());
            aiReply = chatAiClient.invoke(prompt);
        } catch (Exception e) {
            aiReply = "죄송해요! 잠시 오류가 발생했어요. 다시 시도해주세요.";
        }
        ChatMessage aiMessage = ChatMessage.builder()
                .chatRoom(room)
                .role(ChatMessage.Role.ASSISTANT)
                .content(aiReply)
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(aiMessage);

        // 채팅방 요약 업데이트
        room.setLastMessagePreview(req.getMessage());
        room.setLastMessageAt(LocalDateTime.now());

        return createResponseDto(roomId);
    }

    // ======================================================
    // 4) 특정 채팅방 상세 조회
    // ======================================================
    public ChatResponseDto getChatRoomDetail(Long userId, Long roomId) {
        return createResponseDto(roomId);
    }

    // ======================================================
    // ⭐ 5) 채팅방 캐릭터 변경 (ENUM 적용)
    // ======================================================
    @Transactional
    public ChatRoomSummaryDto updateCharacter(Long userId, Long roomId, CharacterType character) {

        ChatRoom room = chatRoomRepository.findByIdAndUserId(roomId, userId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        room.setCharacter(character);

        return new ChatRoomSummaryDto(
                room.getId(),
                room.getTitle(),
                room.getLastMessagePreview(),
                room.getCharacter()
        );
    }

    // ======================================================
    // 내부 공통 응답 생성 (기존 그대로)
    // ======================================================
    private ChatResponseDto createResponseDto(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        List<ChatMessage> messageEntities =
                chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);

        // Entity → DTO 변환
        List<ChatMessageDto> messageDtos = messageEntities.stream()
                .map(m -> ChatMessageDto.builder()
                        .id(m.getId())
                        .role(m.getRole().name())
                        .content(m.getContent())
                        .createdAt(m.getCreatedAt())
                        .build()
                )
                .toList();

        // 마지막 AI 메시지 찾기
        String lastAiContent = messageEntities.stream()
                .filter(m -> m.getRole() == ChatMessage.Role.ASSISTANT)
                .reduce((first, second) -> second)  // 마지막 요소 선택
                .map(ChatMessage::getContent)
                .orElse(null);

        return ChatResponseDto.builder()
                .roomId(room.getId())
                .roomTitle(room.getTitle())
                .messages(messageDtos)
                .lastAiMessage(lastAiContent)
                .build();
    }
    private String buildPrompt(Long userId, String userMessage) {

        // 이번 달 목표 조회
        LocalDate month = LocalDate.now().withDayOfMonth(1);
        Goal goal = goalService.getMonthlyGoal(userId, month);
        String goalText = (goal != null) ? goal.getGoal() : "목표 없음";

        // 최근 소비 5개 조회
        List<Transaction> recent = transactionRepository
                .findByUserId(userId)
                .stream()
                .sorted(Comparator.comparing(Transaction::getDateTime).reversed())
                .limit(5)
                .toList();

        String recentText = recent.stream()
                .map(t -> t.getItemName() + "(" + t.getPrice() + "원)")
                .collect(Collectors.joining(", "));

        return """
                [이번 달 목표]: %s
                [최근 소비]: %s
                
                사용자 말:
                %s
                
                → 위 정보를 참고해서,
                목표 방향에 맞춰 부드럽고 티키타카 느낌으로 대답해주세요
                """.formatted(goalText, recentText, userMessage);
    }

    }
