package com.example.demo.calendar.dto;

import java.time.LocalDateTime;

public class TransactionRequest {
    public String category;
    public String incomeOrExpense;
    public String itemName;
    public Integer price;
    public String planType;
    public String memo;
    public LocalDateTime dateTime;
}

// 스프링이 자동으로 매핑해서 망가뜨리게 X, 문자열로 받아서 직접 ENUM으로 변환
