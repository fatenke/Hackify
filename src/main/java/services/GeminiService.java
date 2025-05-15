package services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeminiService {
    private String apiKey;

    public GeminiService(String apiKey) {
        this.apiKey = "AIzaSyAdtU0BkTPvbpbKhK1J6AGNaSwaywhByZc";
    }
    public GeminiService() {
        // This constructor uses the default API key
    }
    public String getResponse(String userInput) {
        String endpointurl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        // Build JSON request
        JsonObject requestJson = new JsonObject();
        JsonArray contentsArray = new JsonArray();
        JsonObject contentObject = new JsonObject();
        JsonArray partsArray = new JsonArray();
        JsonObject partObject = new JsonObject();

        partObject.addProperty("text", userInput);
        partsArray.add(partObject);
        contentObject.add("parts", partsArray);
        contentsArray.add(contentObject);

        requestJson.add("contents", contentsArray);

        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 1);
        generationConfig.addProperty("topK", 0);
        generationConfig.addProperty("topP", 0.95);
        generationConfig.addProperty("maxOutputTokens", 8192);
        requestJson.add("generationConfig", generationConfig);

        JsonObject safetySettings = new JsonObject();
        safetySettings.addProperty("category", "HARM_CATEGORY_HARASSMENT");
        safetySettings.addProperty("threshold", "BLOCK_MEDIUM_AND_ABOVE");
        requestJson.add("safetySettings", safetySettings);

        try {
            URL url = new URL(endpointurl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("X-Goog-User-Agent", "gl-java/1.0");
            con.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(requestJson.toString().getBytes());
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                String json = response.toString();
                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                JsonArray candidatesArray = jsonObject.getAsJsonArray("candidates");
                for (JsonElement candidateElement : candidatesArray) {
                    JsonObject candidateObject = candidateElement.getAsJsonObject();
                    JsonObject contentObjectResponse = candidateObject.getAsJsonObject("content");
                    JsonArray partsArrayResponse = contentObjectResponse.getAsJsonArray("parts");
                    for (JsonElement partElement : partsArrayResponse) {
                        JsonObject partObjectResponse = partElement.getAsJsonObject();
                        String text = partObjectResponse.get("text").getAsString();
                        return text;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    System.out.println("Error response from server: " + response.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No response from Gemini API";
    }
}