package services;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

public class ModerationService {
    private final String apiKey;
    private final String endpoint = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=";

    public ModerationService(String apiKey) {
        this.apiKey = "AIzaSyCDjamjzNWSoRSWW2AIC0CUI0XhurocBR0";
    }

    /**
     * Sends the given text to Perspective API and returns the toxicity score.
     * A score is between 0.0 (not toxic) and 1.0 (extremely toxic).
     */
    public double getToxicityScore(String text) {
        try {
            URL url = new URL(endpoint + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Build JSON payload
            JsonObject requestPayload = new JsonObject();
            JsonObject comment = new JsonObject();
            comment.addProperty("text", text);
            requestPayload.add("comment", comment);

            // Specify language
            JsonArray languages = new JsonArray();
            languages.add("en");
            languages.add("fr");
            languages.add("ar");
            requestPayload.add("languages", languages);

            // Request the TOXICITY attribute
            JsonObject requestedAttributes = new JsonObject();
            requestedAttributes.add("TOXICITY", new JsonObject());
            requestedAttributes.add("INSULT", new JsonObject());
            requestedAttributes.add("PROFANITY", new JsonObject());
            requestedAttributes.add("IDENTITY_ATTACK", new JsonObject());
            requestedAttributes.add("THREAT", new JsonObject());
            requestPayload.add("requestedAttributes", requestedAttributes);

            // Write payload
            OutputStream os = connection.getOutputStream();
            os.write(requestPayload.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Perspective API HTTP error code: " + responseCode);
                return 0.0;
            }

            // Read response
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                responseBuilder.append(output);
            }
            connection.disconnect();
            // Parse the JSON response to extract scores from all requested attributes
            JsonObject responseJson = JsonParser.parseString(responseBuilder.toString()).getAsJsonObject();
            JsonObject attributeScores = responseJson.getAsJsonObject("attributeScores");
            double maxScore = 0.0;

            // Iterate over each attribute to determine the maximum score.
            Set<String> attributeKeys = attributeScores.keySet();
            for (String key : attributeKeys) {
                JsonObject attribute = attributeScores.getAsJsonObject(key);
                JsonObject summaryScore = attribute.getAsJsonObject("summaryScore");
                double score = summaryScore.get("value").getAsDouble();
                if (score > maxScore) {
                    maxScore = score;
                }
            }
            return maxScore;
        } catch (Exception e) {
            System.err.println("Error in getToxicityScore: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
}
