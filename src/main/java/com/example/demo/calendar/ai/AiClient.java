package com.example.demo.calendar.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

@Component
public class AiClient {
    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.mock:false}")   // 🔥 추가
    private boolean mock;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String invoke(String prompt) throws Exception {

        OkHttpClient client = new OkHttpClient();

        JSONObject requestJson = new JSONObject();
        requestJson.put("model", model);

        JSONArray messages = new JSONArray();

        // 1. System Message (JSON 반환 강제 조건 충족)
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful financial consumption analysis assistant. Your response MUST be a valid JSON object.");
        messages.put(systemMessage);

        // 2. User Message (prompt는 라이브러리에 의해 자동 이스케이프됨)
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.put(userMessage);

        requestJson.put("messages", messages);

        // JSON 응답 형식 강제 설정 (GPT-4o 요구사항)
        JSONObject responseFormat = new JSONObject();
        responseFormat.put("type", "json_object");
        requestJson.put("response_format", responseFormat);

        // 최종 요청 본문 문자열 (라이브러리가 유효한 JSON을 보장함)
        String requestBody = requestJson.toString();

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody))
                .build();

        Response response = client.newCall(request).execute();
        String raw = response.body().string();

        if (!response.isSuccessful()) {
            // 실패 시 OpenAI의 오류 JSON 출력
            throw new Exception("OpenAI API 호출 실패 (HTTP " + response.code() + "): " + raw);
        }

        JSONObject json = new JSONObject(raw);
        String content = json
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        return content;
    }
}
