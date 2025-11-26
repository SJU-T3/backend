package com.example.demo.calendar.service;

import com.example.demo.calendar.ai.AiClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiGoalMatchService {
    private final AiClient aiClient;

    public boolean isMatched(String goalName, String itemName) {
        try {
            String prompt = """
                    소비 항목이 아래 목표에 해당하면 {"match": true},
                    아니면 {"match": false} 로 ONLY JSON 반환.
                    
                    목표: %s
                    소비 항목: %s
                    """
                    .formatted(goalName, itemName);

            String result = aiClient.invoke(prompt);
            System.out.println("[AI] goal = " + goalName + ", item = " + itemName);
            System.out.println("[AI] prompt sent");
            System.out.println("[AI] response = " + result);
            return new JSONObject(result).getBoolean("match");

        } catch (Exception e) {
            return false;
        }
    }
}
