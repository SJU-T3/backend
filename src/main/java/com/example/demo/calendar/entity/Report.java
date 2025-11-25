package com.example.demo.calendar.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String month;  // "2025-01" 형태로 저장

    private LocalDateTime uptoDate; // 마지막 생성 시간

    @Column(columnDefinition = "TEXT")
    private String summary;  // GPT가 준 소비 리포트 요약

    @Column(columnDefinition = "TEXT")
    private String categoryFeedback; // JSON string

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public LocalDateTime getUptoDate() { return uptoDate; }
    public void setUptoDate(LocalDateTime uptoDate) { this.uptoDate = uptoDate; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getCategoryFeedback() { return categoryFeedback; }
    public void setCategoryFeedback(String categoryFeedback) { this.categoryFeedback = categoryFeedback; }
}
