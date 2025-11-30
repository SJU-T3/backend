package com.example.demo.calendar.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "income_or_expense")
    private IncomeType incomeType;

    @Enumerated(EnumType.STRING)
    private CategoryType category;

    private String itemName;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    private String memo;

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

    // ============================
    // Getter / Setter 필수 부분
    // ============================

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public IncomeType getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(IncomeType incomeType) {
        this.incomeType = incomeType;
    }
    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    // ================================
    // ENUMS
    // ================================

    public enum IncomeType {
        INCOME, EXPENSE
    }

    public enum PlanType {
        IMPULSE, PLANNED
    }

    public enum CategoryType {

        // 소비 카테고리
        FOOD("식비"),
        TRANSPORT("교통비"),
        HOUSING("주거/공과금"),
        EDUCATION("교육"),
        HEALTH("건강/의료"),
        HOBBY("취미/여가"),
        FASHION("패션"),
        DRINK("술/유흥"),
        EVENT("경조사"),
        TRAVEL("여행/숙박"),
        DAILY_NECESSITIES("생필품"),
        FINANCE("금융/보험/세금/통신비"),
        ETC_EXPENSE("기타 지출"),

        // 수입 카테고리
        SALARY("월급"),
        ALLOWANCE("용돈"),
        REFUND("환불"),
        INTEREST("이자/적금만기"),
        ETC("기타"),
        SIDE("부수입");

        private final String displayName;

        CategoryType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
