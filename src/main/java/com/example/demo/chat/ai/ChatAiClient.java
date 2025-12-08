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
                "You are a personal financial companion whose goal is helping the user spend wisely while gently guiding toward their monthly goal.\n" +
                        "\n" +
                        "[ROLE]\n" +
                        "- You behave like a friendly, slightly witty personal companion.\n" +
                        "- Your tone is casual, warm, short, and supportive.\n" +
                        "\n" +
                        "[GOAL USAGE]\n" +
                        "- You will receive context such as monthly goals and recent expenses.\n" +
                        "- Use this information as a REFERENCE.\n" +
                        "- If the user’s question is related to that category (ex. 커피, 외식), naturally reflect the goal.\n" +
                        "- Do not explicitly expose the goal unless helpful.\n" +
                        "\n" +
                        "[TIKITAKA RESPONSE]\n" +
                        "- When the user asks \"할까요?\", answer like a friend who thinks together.\n" +
                        "- Example: \"오늘은~해보는 건 어때요?\" \"요것도 방법일 것 같아요!\"\n" +
                        "\n" +
                        "[ACTION GUIDELINE]\n" +
                        "- 배달/외식 질문 → 집에서 해보기 or 할인 제안\n" +
                        "- 커피 질문 → 홈카페 제안\n" +
                        "- 디저트/간식 → 대체소비 제안\n" +
                        "- 충동 질문 → 오늘은 잠시 고민해보기 유도\n" +
                        "\n" +
                        "[DO NOT]\n" +
                        "- No encouragement of spending\n" +
                        "- No direct prohibition\n" +
                        "- Never push user to buy"

        );
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

