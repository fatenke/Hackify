package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeminiApiController {

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyDMLUGm6gwHHEJRAdzngDTy2CXFe09n-QY";

    @FXML
    private TextField questionField;

    @FXML
    private TextArea responseArea;

    @FXML
    private Button sendButton;

    @FXML
    public void handleSendRequest() {
        String question = questionField.getText().trim();
        if (question.isEmpty()) {
            responseArea.setText("Please enter a question!");
            return;
        }

        String jsonInputString = "{" +
                "\"contents\": [{" +
                "\"parts\":[{\"text\": \"" + question + "\"}]" +
                "}]" +
                "}";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(API_URL);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonInputString));

            HttpResponse response = client.execute(httpPost);
            String responseString = EntityUtils.toString(response.getEntity());

            // Print the raw response for debugging


            JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();

            // Check if the response contains candidates and parse accordingly
            if (jsonResponse.has("candidates") && !jsonResponse.getAsJsonArray("candidates").isEmpty()) {
                String resultText = jsonResponse.getAsJsonArray("candidates").get(0)
                        .getAsJsonObject().getAsJsonObject("content")
                        .getAsJsonArray("parts").get(0)
                        .getAsJsonObject().get("text").getAsString();
                responseArea.setText("Response: " + resultText);
            } else {
                responseArea.setText("No valid content in the response.");
            }

        } catch (Exception e) {
            responseArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
