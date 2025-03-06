package services;

import Interfaces.GlobalInterface;
import javafx.scene.control.Alert;
import models.Chat;
import models.ChatType;
import models.Communaute;
import models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements GlobalInterface<Message> {
    private  Connection conn;
    private  BotService botService;

    public MessageService(Connection conn,
                          ChatService chatService,
                          GeminiService geminiService) {
        this.conn = conn;
        this.botService = new BotService(this, chatService, geminiService);
    }

    @Override
    public void add(Message message) {
        // Initialize ModerationService with the API key from your environment variables
        ModerationService moderationService = new ModerationService("AIzaSyBD2OhIz6EAxRE9tf4U6ZZ4-G8FBhjjCXY");
        double toxicityScore = moderationService.getToxicityScore(message.getContenu());
        double threshold = 0.8; // Set your toxicity threshold here

        // If toxicity score exceeds the threshold, flag and do not save the message.
        if (toxicityScore >= threshold) {
            System.out.println("Message flagged as toxic (score: " + toxicityScore + "). Not saving message.");
            // Display a JavaFX alert to the user:
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Message Blocked");
            alert.setHeaderText("Inappropriate Content Detected");
            alert.setContentText("Your message was flagged as inappropriate and has not been sent.");
            alert.showAndWait();
            return;
        }


        // Save the message to the database.
        saveMessage(message);

        // If the message is not from the bot (BOT_USER_ID should be defined consistently across your services),
        // then let the bot handle the user message.
        if (message.getPostedBy() != BotService.BOT_USER_ID) {
            botService.handleUserMessage(message);
        }
    }

    private void saveMessage(Message message) {
        String sql = "INSERT INTO message (chat_id, posted_by, contenu, type, post_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getChatId());
            stmt.setInt(2, message.getPostedBy());
            stmt.setString(3, message.getContenu());
            stmt.setString(4, message.getType().name()); // Convert enum to String
            stmt.setTimestamp(5, message.getPostTime());
            stmt.executeUpdate();

            // Retrieve the generated ID and set it in the message
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving message: " + e.getMessage());
        }
    }

    @Override
    public void update(Message message) {
        String sql = "UPDATE message SET contenu = ?, type = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, message.getContenu());
            stmt.setString(2, message.getType().name());
            stmt.setInt(3, message.getId());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Message mis à jour avec succès !");
            } else {
                System.out.println("Aucun message trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public List<Message> getAll() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Debugging Print Statements
                System.out.println("Fetched ID: " + rs.getInt("id"));
                System.out.println("Fetched chatId: " + rs.getInt("chat_id"));
                System.out.println("Fetched postedBy: " + rs.getInt("posted_by"));
                System.out.println("Fetched contenu: " + rs.getString("contenu"));
                System.out.println("Fetched type: " + rs.getString("type"));
                System.out.println("Fetched postTime: " + rs.getTimestamp("post_time"));

                messages.add(new Message(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getString("contenu"),
                        Message.MessageType.valueOf(rs.getString("type")),
                        rs.getTimestamp("post_time"),
                        rs.getInt("posted_by")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des messages : " + e.getMessage());
        }
        return messages;
    }


    @Override
    public void delete(Message message) {
        String sql = "DELETE FROM message WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, message.getId());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Message supprimé avec succès !");
            } else {
                System.out.println("Aucun message trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }



    public Message getById(int messageId) {
        String sql = "SELECT * FROM message WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Message(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getString("contenu"),
                        Message.MessageType.valueOf(rs.getString("type")),
                        rs.getTimestamp("post_time"),
                        rs.getInt("posted_by"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du message : " + e.getMessage());
        }
        return null;
    }

    public List<Message> getMessagesByChatId(int chatId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE chat_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getString("contenu"),
                        Message.MessageType.valueOf(rs.getString("type")),
                        rs.getTimestamp("post_time"),
                        rs.getInt("posted_by")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des messages : " + e.getMessage());
        }
        return messages;
    }


    public List<Message> searchMessages(String searchTerm) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE contenu LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message message = new Message(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getString("contenu"),
                        Message.MessageType.valueOf(rs.getString("type")),
                        rs.getTimestamp("post_time"),
                        rs.getInt("posted_by")
                );
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println("Error searching messages: " + e.getMessage());
        }
        return messages;
    }

}

