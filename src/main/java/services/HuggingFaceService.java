package services;
import okhttp3.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;

public class HuggingFaceService {

    private static final String API_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.3";
    private static final String API_TOKEN = "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";
    // Using environment variable for security

    public String generateChapterContent(String chapterTitle) {
        HttpURLConnection connection = null;

        if (API_TOKEN == null || API_TOKEN.isEmpty()) {
            return "Erreur : Clé API manquante.";
        }

        try {
            URL obj = new URL(API_URL);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(15000);  // Timeout for establishing connection
            connection.setReadTimeout(30000);     // Timeout for reading response

            // Creating prompt for the model
            String prompt = chapterTitle + "\n\n"
                    + "Introduction : \n"
                    + "Définition et importance du sujet.\n\n"
                    + "Contexte historique : \n"
                    + "Évolution et développements majeurs.\n\n"
                    + "Principes et applications : \n"
                    + "Description scientifique et domaines d’application.\n\n"
                    + "Implications éthiques : \n"
                    + "Conséquences et considérations morales.\n\n"
                    + "Conclusion : \n"
                    + "Synthèse et perspectives d’avenir.";

            JSONObject requestBody = new JSONObject();
            requestBody.put("inputs", prompt);
            requestBody.put("parameters", new JSONObject()
                    .put("max_length", 600)
                    .put("temperature", 0.4)
                    .put("top_p", 0.85));

            // Sending request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Reading response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                try {
                    // Handle response as either JSON array or object
                    if (response.toString().trim().startsWith("[")) {
                        JSONArray jsonResponseArray = new JSONArray(response.toString());
                        if (jsonResponseArray.length() > 0) {
                            JSONObject firstItem = jsonResponseArray.getJSONObject(0);
                            if (firstItem.has("generated_text")) {
                                return nettoyerTexte(firstItem.getString("generated_text"));
                            }
                        }
                    } else {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.has("generated_text")) {
                            return nettoyerTexte(jsonResponse.getString("generated_text"));
                        }
                    }
                    return "Erreur : La clé 'generated_text' n'est pas trouvée dans la réponse.";
                } catch (JSONException e) {
                    return "Erreur de format JSON dans la réponse de l'API : " + e.getMessage();
                }
            } else {
                return "Erreur de connexion avec l'API. Code de réponse: " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur de communication avec l'API.";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Clean unnecessary instructions and keep academic content
    private String nettoyerTexte(String texte) {
        texte = texte.replaceAll("(?i)rédige un texte académique structuré sur le sujet suivant.*?:", "").trim();
        texte = texte.replaceAll("(?i)le texte doit être formel.*?:", "").trim();
        return texte;
    }
}
