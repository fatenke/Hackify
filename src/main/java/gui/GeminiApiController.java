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
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

        String databaseResponse = handleDatabaseQueries(question);

        if (!databaseResponse.isEmpty()) {
            responseArea.setText("Database Response:\n" + databaseResponse);
        } else {
            sendQuestionToGemini(question);
        }
    }

    private String handleDatabaseQueries(String question) {
        try (Connection connection = MySQLConnection.getConnection()) {
            if (question.toLowerCase().contains("how many evaluations")) {
                return "Total evaluations: " + getCount(connection, "evaluation");
            } else if (question.toLowerCase().contains("how many votes")) {
                return "Total votes: " + getCount(connection, "vote");
            } else if (question.toLowerCase().contains("list of evaluations")) {
                return getAllEvaluations(connection);
            } else if (question.toLowerCase().contains("list of votes")) {
                return getAllVotes(connection);
            } else if (question.toLowerCase().contains("evaluation with the id")) {
                int id = extractId(question);
                return getEvaluationById(connection, id);
            } else if (question.toLowerCase().contains("vote with the id")) {
                int id = extractId(question);
                return getVoteById(connection, id);
            } else if (question.toLowerCase().contains("highest note technique")) {
                return "Highest technical score: " + getMaxScore(connection, "noteTech", "evaluation");
            } else if (question.toLowerCase().contains("highest note d'innovation")) {
                return "Highest innovation score: " + getMaxScore(connection, "noteInnov", "evaluation");
            } else if (question.toLowerCase().contains("average note technique")) {
                return "Average technical score: " + getAvgScore(connection, "noteTech", "evaluation");
            } else if (question.toLowerCase().contains("average note d'innovation")) {
                return "Average innovation score: " + getAvgScore(connection, "noteInnov", "evaluation");
            } else if (question.toLowerCase().contains("average valeur de vote")) {
                return "Average vote value: " + getAvgScore(connection, "valeurVote", "vote");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database error: " + e.getMessage();
        }
        return "Question not recognized!";
    }

    private int getCount(Connection connection, String table) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM " + table;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt("count");
        }
        return 0;
    }

    private String getAllEvaluations(Connection connection) throws SQLException {
        return getAllRows(connection, "SELECT * FROM evaluation");
    }

    private String getAllVotes(Connection connection) throws SQLException {
        return getAllRows(connection, "SELECT * FROM vote");
    }

    private String getEvaluationById(Connection connection, int id) throws SQLException {
        return getRowById(connection, "SELECT * FROM evaluation WHERE id = ?", id);
    }

    private String getVoteById(Connection connection, int id) throws SQLException {
        return getRowById(connection, "SELECT * FROM vote WHERE id = ?", id);
    }

    private float getMaxScore(Connection connection, String column, String table) throws SQLException {
        String query = "SELECT MAX(" + column + ") AS maxScore FROM " + table;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getFloat("maxScore");
        }
        return 0;
    }

    private float getAvgScore(Connection connection, String column, String table) throws SQLException {
        String query = "SELECT AVG(" + column + ") AS avgScore FROM " + table;
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getFloat("avgScore");
        }
        return 0;
    }

    private String getAllRows(Connection connection, String query) throws SQLException {
        StringBuilder result = new StringBuilder();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(metaData.getColumnName(i)).append(": ")
                            .append(rs.getString(i)).append(" | ");
                }
                result.append("\n");
            }
        }
        return result.toString();
    }

    private String getRowById(Connection connection, String query, int id) throws SQLException {
        StringBuilder result = new StringBuilder();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            if (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(metaData.getColumnName(i)).append(": ")
                            .append(rs.getString(i)).append(" | ");
                }
            }
        }
        return result.toString();
    }

    private int extractId(String question) {
        String idString = question.replaceAll("\\D", "");
        return idString.isEmpty() ? -1 : Integer.parseInt(idString);
    }

    private void sendQuestionToGemini(String question) {
        responseArea.setText("Sending to Gemini API: " + question);
    }
}
