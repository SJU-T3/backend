package com.example.demo.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    @Column(length = 200)
    private String lastMessagePreview;

    private LocalDateTime lastMessageAt;

    private LocalDateTime createdAt;

    // ⭐ ENUM 적용
    @Enumerated(EnumType.STRING)
    private CharacterType character;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
