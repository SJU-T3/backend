package com.example.demo.calendar.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class DaySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDate date;

    private Integer totalIncome = 0;
    private Integer totalExpense = 0;

    private Integer balance = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --------------------------
    // 수입/지출 업데이트 로직
    // --------------------------

    public void addIncome(int amount) {
        this.totalIncome += amount;
        updateBalance();
    }

    public void addExpense(int amount) {
        this.totalExpense += amount;
        updateBalance();
    }

    private void updateBalance() {
        this.balance = this.totalIncome - this.totalExpense;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --------------------------
    // Getter & Setter (중요)
    // --------------------------

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getTotalIncome() {
        return totalIncome;
    }

    public Integer getTotalExpense() {
        return totalExpense;
    }

    public Integer getBalance() {
        return balance;
    }
}
