package com.example.demo.calendar.service;
import com.example.demo.calendar.entity.Goal;
import com.example.demo.calendar.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.demo.calendar.service.AiGoalMatchService;
import java.util.Optional;


import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final AiGoalMatchService aiGoalMatchService;
    public void setMonthlyGoal(Long userId, LocalDate month, String goalName, int targetCount) {

        LocalDate monthKey = month.withDayOfMonth(1);

        Goal goal = goalRepository.findByUserIdAndMonth(userId, monthKey)
                .orElseGet(() -> {
                    Goal g = new Goal();
                    g.setUserId(userId);
                    g.setMonth(monthKey);
                    return g;
                });

        goal.setGoal(goalName);
        goal.setTargetCount(targetCount);
        goal.setCurrentCount(0);  // 항상 새 목표는 초기화

        goalRepository.save(goal);
    }

    /**
     * 소비가 추가될 때 목표와 AI 매칭해 currentCount 증가
     */
    public void checkAndIncrease(Long userId, String itemName) {


        LocalDate monthKey = LocalDate.now().withDayOfMonth(1);

        System.out.println("[GOAL] Checking goal for userId=" + userId);
        System.out.println("[GOAL] Searching monthKey=" + monthKey);

        Goal goal = goalRepository.findByUserIdAndMonth(userId, monthKey)
                .orElse(null);  // 목표 없으면 그냥 패스

        if (goal == null) {
            System.out.println("[GOAL] No goal found for this user/month");
            return;
        }

        boolean matched = aiGoalMatchService.isMatched(
                goal.getGoal(),
                itemName
        );

        if (matched) {
            goal.increase();
            goalRepository.save(goal);
        }
    }

    public Goal getMonthlyGoal(Long userId, LocalDate month) {
        LocalDate monthKey = month.withDayOfMonth(1);
        return goalRepository.findByUserIdAndMonth(userId, monthKey).orElse(null);
    }

}
