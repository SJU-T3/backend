package com.example.demo.calendar.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class MonthlySummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDate month;   // 2025-02-01 형태

    private Integer totalGoalAmount;     // 총 소비 목표 금액
    private Integer totalExpenseAmount;  // 총 지출 금액
    private Integer totalIncomeAmount;   // 총 수입 금액

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MonthlySummary() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public Integer getTotalGoalAmount() {
        return totalGoalAmount;
    }

    public void setTotalGoalAmount(Integer totalGoalAmount) {
        this.totalGoalAmount = totalGoalAmount;
    }

    public Integer getTotalExpenseAmount() {
        return totalExpenseAmount;
    }

    public void setTotalExpenseAmount(Integer totalExpenseAmount) {
        this.totalExpenseAmount = totalExpenseAmount;
    }

    public Integer getTotalIncomeAmount() {
        return totalIncomeAmount;
    }

    public void setTotalIncomeAmount(Integer totalIncomeAmount) {
        this.totalIncomeAmount = totalIncomeAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
