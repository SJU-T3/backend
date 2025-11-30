package com.example.demo.chat.ai;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChatAiClient {
    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String invoke(String message) throws Exception {

        OkHttpClient client = new OkHttpClient();

        JSONObject reqJson = new JSONObject();
        reqJson.put("model", model);

        JSONArray messages = new JSONArray();

        JSONObject system = new JSONObject();
        system.put("role", "system");
        system.put("content",
                "You are a warm and friendly personal finance assistant. " +
                        "Answer in natural conversational Korean.");
        messages.put(system);

        JSONObject user = new JSONObject();
        user.put("role", "user");
        user.put("content", message);
        messages.put(user);

        reqJson.put("messages", messages);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json"), reqJson.toString()))
                .build();

        Response response = client.newCall(request).execute();
        String raw = response.body().string();

        if (!response.isSuccessful()) {
            throw new Exception("OpenAI 호출 실패: " + raw);
        }

        JSONObject json = new JSONObject(raw);
        return json
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}

