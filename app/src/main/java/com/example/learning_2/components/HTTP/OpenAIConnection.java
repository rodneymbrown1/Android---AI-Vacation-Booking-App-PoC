package com.example.learning_2.components.HTTP;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OpenAIConnection {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";  // ⚠️ REPLACE with a valid key
    private static final String ORG_ID = "org-48vje6tb4qWLUYQ9t6VMdKEL";

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * Sends a CORS preflight request to test API accessibility.
     */
    public static void sendPreflightRequest() {
        try {
            Request request = new Request.Builder()
                    .url(API_URL)
                    .method("OPTIONS", null)
                    .header("Access-Control-Request-Method", "POST")
                    .header("Access-Control-Request-Headers", "Content-Type, Authorization")
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println("🔹 Preflight Response: " + response.code() + " " + response.body().string());
        } catch (IOException e) {
            System.err.println("❌ Preflight Request Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Fetches a response from OpenAI API with proper JSON formatting and error handling.
     */
    public static String fetchOpenAIResponse(String userMessage) {
        try {
            // Construct JSON request body
            JSONObject json = new JSONObject();
            json.put("model", "gpt-4o-mini");

            // Add messages array
            JSONArray messages = new JSONArray();
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);

            json.put("messages", messages);
            json.put("temperature", 0.7);

            // Print the JSON request body for debugging
            System.out.println("📤 Request JSON: " + json.toString(2));

            // Build the HTTP request
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("OpenAI-Organization", ORG_ID)
                    .header("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // Print the API response
            System.out.println("📥 API Response: " + responseBody);

            // Check if OpenAI returned an error message
            JSONObject responseObject = new JSONObject(responseBody);
            if (responseObject.has("error")) {
                return "❌ OpenAI API Error: " + responseObject.getJSONObject("error").getString("message");
            }

            // Parse and return the AI response content
            return responseObject.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }

    /**
     * Main function to test API connectivity and responses.
     */
    public static void main(String[] args) {
        try {
            sendPreflightRequest(); // Send CORS preflight request
            String response = fetchOpenAIResponse("Hello! Generate three travel excursions.");
            System.out.println("✅ AI Response: " + response);
        } catch (Exception e) {
            System.err.println("❌ API Call Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
