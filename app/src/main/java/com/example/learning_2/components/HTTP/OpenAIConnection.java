package com.example.learning_2.components.HTTP;

import com.example.learning_2.BuildConfig;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Legacy Java HTTP client for OpenAI. Prefer AiService.kt for new code.
 * API key is injected via BuildConfig — never hardcode credentials in source.
 */
public class OpenAIConnection {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public static String fetchOpenAIResponse(String userMessage) {
        try {
            JSONObject json = new JSONObject();
            json.put("model", "gpt-4o-mini");

            JSONArray messages = new JSONArray();
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);

            json.put("messages", messages);
            json.put("temperature", 0.7);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                    .header("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY)
                    .header("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            JSONObject responseObject = new JSONObject(responseBody);
            if (responseObject.has("error")) {
                return "Error: " + responseObject.getJSONObject("error").getString("message");
            }

            return responseObject.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
