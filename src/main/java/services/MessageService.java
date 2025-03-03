package services;

import Interfaces.GlobalInterface;
import models.Communaute;
import models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageService implements GlobalInterface<Message> {
    private Connection conn;

    public MessageService(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void add(Message message) {
        String sql = "INSERT INTO message (chat_id, posted_by, contenu, type, post_time) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getChatId());
            stmt.setInt(2, message.getPostedBy());
            stmt.setString(3, message.getContenu());
            stmt.setString(4, message.getType().name()); // Store ENUM as String
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
            System.out.println("Message ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du message : " + e.getMessage());
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


}

