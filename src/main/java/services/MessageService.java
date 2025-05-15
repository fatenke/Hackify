package services;

import Interfaces.GlobalInterface;
import javafx.scene.control.Alert;
import models.Message;
import models.Participation;
import models.User;
import util.MyConnection;
import util.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class MessageService implements GlobalInterface<Message> {
    private final Connection conn;
    private final BotService botService;

    public MessageService(ChatService chatService, GeminiService geminiService) {
        this.conn = MyConnection.getInstance().getConnection();
        if (this.conn == null) {
            throw new IllegalStateException("Connexion à la base de données non établie.");
        }
        this.botService = new BotService(this, chatService, geminiService);
    }



    @Override
    public void add(Message message) {
        // Initialize ModerationService with the API key
        ModerationService moderationService = new ModerationService("AIzaSyBD2OhIz6EAxRE9tf4U6ZZ4-G8FBhjjCXY");
        double toxicityScore = moderationService.getToxicityScore(message.getContenu());
        double threshold = 0.8; // Set your toxicity threshold

        // Récupérer l'utilisateur connecté
        User userConnecte = SessionManager.getSession(SessionManager.getLastSessionId());
        if (userConnecte == null) {
            out.println("Aucun utilisateur connecté !");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Utilisateur Non Connecté");
            alert.setContentText("Vous devez être connecté pour envoyer un message.");
            alert.showAndWait();
            return;
        }

        // Set the posted_by field to the current user's ID
        message.setPostedBy(userConnecte.getId());

        // If toxicity score exceeds the threshold, flag and do not save the message
        if (toxicityScore >= threshold) {
            out.println("Message signalé comme toxique (score : " + toxicityScore + "). Message non enregistré.");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Message Bloqué");
            alert.setHeaderText("Contenu Inapproprié Détecté");
            alert.setContentText("Votre message a été jugé inapproprié et n'a pas été envoyé.");
            alert.showAndWait();
            return;
        }

        // Save the message to the database
        saveMessage(message);

        // If the message is not from the bot, let the bot handle it
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
            stmt.setString(4, "MESSAGE"); // Default type for user messages
            stmt.setTimestamp(5, message.getPostTime());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'enregistrement du message : " + e.getMessage());
            throw new RuntimeException("Échec de l'enregistrement du message", e);
        }
    }
    @Override
    public void update(Message message) {
        String sql = "UPDATE message SET chat_id = ?, posted_by = ?, contenu = ?, post_time = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, message.getChatId());
            stmt.setInt(2, message.getPostedBy());
            stmt.setString(3, message.getContenu());
            stmt.setTimestamp(4, message.getPostTime());
            stmt.setInt(5, message.getId());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                out.println("Message mis à jour avec succès !");
            } else {
                out.println("Aucun message trouvé avec l'ID : " + message.getId());
            }
        } catch (SQLException e) {
            out.println("Erreur lors de la mise à jour du message : " + e.getMessage());
        }
    }

    @Override
    public List<Message> getAll() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                messages.add(new Message(
                        rs.getInt("id"),
                        rs.getInt("chat_id"),
                        rs.getString("contenu"),
                        rs.getTimestamp("post_time"),
                        rs.getInt("posted_by")
                ));
            }
        } catch (SQLException e) {
            out.println("Erreur lors de la récupération des messages : " + e.getMessage());
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
                out.println("Message supprimé avec succès !");
            } else {
                out.println("Aucun message trouvé avec l'ID : " + message.getId());
            }
        } catch (SQLException e) {
            out.println("Erreur lors de la suppression du message : " + e.getMessage());
        }
    }

    public Message getById(int messageId) {
        String sql = "SELECT * FROM message WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                            rs.getInt("id"),
                            rs.getInt("chat_id"),
                            rs.getString("contenu"),
                            rs.getTimestamp("post_time"),
                            rs.getInt("posted_by")
                    );
                }
            }
        } catch (SQLException e) {
            out.println("Erreur lors de la récupération du message : " + e.getMessage());
        }
        return null;
    }

    public List<Message> getMessagesByChatId(int chatId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE chat_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(
                            rs.getInt("id"),
                            rs.getInt("chat_id"),
                            rs.getString("contenu"),
                            rs.getTimestamp("post_time"),
                            rs.getInt("posted_by")
                    ));
                }
            }
        } catch (SQLException e) {
            out.println("Erreur lors de la récupération des messages : " + e.getMessage());
        }
        return messages;
    }

    public List<Message> searchMessages(String searchTerm) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE contenu LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(
                            rs.getInt("id"),
                            rs.getInt("chat_id"),
                            rs.getString("contenu"),
                            rs.getTimestamp("post_time"),
                            rs.getInt("posted_by")
                    ));
                }
            }
        } catch (SQLException e) {
            out.println("Erreur lors de la recherche des messages : " + e.getMessage());
        }
        return messages;
    }
}