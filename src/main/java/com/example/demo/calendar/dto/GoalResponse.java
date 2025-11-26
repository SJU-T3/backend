package com.example.demo.calendar.dto;

import com.example.demo.calendar.entity.Goal;

public class GoalResponse {
    private String goal;
    private int targetCount;
    private int currentCount;

    public GoalResponse(Goal g) {
        this.goal = g.getGoal();
        this.targetCount = g.getTargetCount();
        this.currentCount = g.getCurrentCount();
    }

    public String getGoal() { return goal; }
    public int getTargetCount() { return targetCount; }
    public int getCurrentCount() { return currentCount; }
}
