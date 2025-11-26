package com.example.demo.calendar.dto;

public class MonthlyCountResponse {
    private long totalExpenseCount;
    private long impulseCount;
    private long plannedCount;

    public MonthlyCountResponse(long totalExpenseCount, long impulseCount, long plannedCount) {
        this.totalExpenseCount = totalExpenseCount;
        this.impulseCount = impulseCount;
        this.plannedCount = plannedCount;
    }

    public long getTotalExpenseCount() {
        return totalExpenseCount;
    }

    public long getImpulseCount() {
        return impulseCount;
    }

    public long getPlannedCount() {
        return plannedCount;
    }
}
