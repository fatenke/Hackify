package services;

import Interfaces.GlobalInterface;
import models.Chat;
import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatService implements GlobalInterface<Chat> {
    private Connection conn;

    public ChatService() {
        conn = MyConnection.getInstance().getConnection();
        if (conn == null) {
            throw new IllegalStateException("Database connection is not established.");
        }
    }

    @Override
    public void add(Chat chat) {
        String sql = "INSERT INTO chat (communaute_id, nom, type, date_creation, is_active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, chat.getCommunauteId());
            stmt.setString(2, chat.getNom());
            stmt.setString(3, chat.getType());
            stmt.setTimestamp(4, chat.getDateCreation());
            stmt.setBoolean(5, chat.isActive());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    chat.setId(generatedKeys.getInt(1)); // Set the generated ID
                }
            }
            System.out.println("Chat ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du chat : " + e.getMessage());
        }
    }

    @Override
    public void update(Chat chat) {
        String sql = "UPDATE chat SET communaute_id = ?, nom = ?, type = ?, date_creation = ?, is_active = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chat.getCommunauteId());
            stmt.setString(2, chat.getNom());
            stmt.setString(3, chat.getType());
            stmt.setTimestamp(4, chat.getDateCreation());
            stmt.setBoolean(5, chat.isActive());
            stmt.setInt(6, chat.getId());
            stmt.executeUpdate();
            System.out.println("Chat mis à jour !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public List<Chat> getAll() {
        List<Chat> chats = new ArrayList<>();
        String sql = "SELECT * FROM chat";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                chats.add(new Chat(
                        rs.getInt("id"),
                        rs.getInt("communaute_id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getTimestamp("date_creation"),
                        rs.getBoolean("is_active")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des chats : " + e.getMessage());
        }
        return chats;
    }

    @Override
    public void delete(Chat chat) {
        String sql = "DELETE FROM chat WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chat.getId());
            stmt.executeUpdate();
            System.out.println("Chat supprimé !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    // DELETE ALL Chats by Community ID
    public void deleteAllByCommunityId(int communauteId) {
        String sql = "DELETE FROM chat WHERE communaute_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, communauteId);
            int rowsDeleted = stmt.executeUpdate();
            System.out.println(rowsDeleted + " chats supprimés pour la communauté ID " + communauteId);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression des chats : " + e.getMessage());
        }
    }

    // Add default chats with specific types and names
    public void addDefaultChats(int communauteId) {
        String sql = "INSERT INTO chat (communaute_id, nom, type, date_creation, is_active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Define the default chats with names and types
            String[][] defaultChats = {
                    {"Annonces", "ANNOUNCEMENT"},
                    {"Questions", "QUESTION"},
                    {"Retour", "FEEDBACK"},
                    {"Coaching", "COACH"},
                    {"Support", "BOT_SUPPORT"}
            };

            Timestamp now = new Timestamp(System.currentTimeMillis());
            for (String[] chatInfo : defaultChats) {
                String nom = chatInfo[0];
                String type = chatInfo[1];
                stmt.setInt(1, communauteId);
                stmt.setString(2, nom);
                stmt.setString(3, type);
                stmt.setTimestamp(4, now);
                stmt.setBoolean(5, true); // All default chats are active
                stmt.executeUpdate();
            }
            System.out.println("Chats par défaut ajoutés pour la communauté ID: " + communauteId);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout des chats par défaut : " + e.getMessage());
        }
    }

    public List<Chat> getChatsByCommunityId(int communauteId) {
        List<Chat> chats = new ArrayList<>();
        String sql = "SELECT * FROM chat WHERE communaute_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, communauteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chats.add(new Chat(
                            rs.getInt("id"),
                            rs.getInt("communaute_id"),
                            rs.getString("nom"),
                            rs.getString("type"),
                            rs.getTimestamp("date_creation"),
                            rs.getBoolean("is_active")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des chats : " + e.getMessage());
        }
        return chats;
    }

    public Chat getChatById(int chatId) {
        String sql = "SELECT * FROM chat WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Chat(
                            rs.getInt("id"),
                            rs.getInt("communaute_id"),
                            rs.getString("nom"),
                            rs.getString("type"),
                            rs.getTimestamp("date_creation"),
                            rs.getBoolean("is_active")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du chat : " + e.getMessage());
        }
        return null;
    }
}