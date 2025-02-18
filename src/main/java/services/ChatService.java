package services;

import Interfaces.GlobalInterface;
import models.Chat;
import models.ChatType;
import util.MyConnection;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class ChatService implements GlobalInterface<Chat> {
    private Connection conn;

    public ChatService() {
        conn = MyConnection.getInstance().getCnx();
    }


    @Override
    public void add(Chat chat) {
        String sql = "INSERT INTO chat (communaute_id, nom, type, date_creation) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, chat.getCommunauteId());
            stmt.setString(2, chat.getNom());
            stmt.setString(3, chat.getType().name());  // Convert Enum to String
            stmt.setTimestamp(4, chat.getDateCreation());
            stmt.executeUpdate();
            System.out.println("Chat ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du chat : " + e.getMessage());
        }
    }

    @Override
    public void update(Chat chat) {
        String sql = "UPDATE chat SET nom = ?, type = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chat.getNom());
            stmt.setString(2, chat.getType().name()); // Enum to String
            stmt.setInt(3, chat.getId());
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
                        ChatType.valueOf(rs.getString("type")),  // Convert String to Enum
                        rs.getTimestamp("date_creation")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des chats : " + e.getMessage());
        }
        return chats;    }

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

//chat par defaut (rooms)
    public void addDefaultChats(int communauteId) {
        String sql = "INSERT INTO chat (communaute_id, nom, type, date_creation) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (ChatType type : ChatType.values()) {
                if (type != ChatType.CUSTOM) { // Ignore CUSTOM type
                    stmt.setInt(1, communauteId);
                    stmt.setString(2, type.name()); // Chat name = type
                    stmt.setString(3, type.name()); // Enum to string
                    stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

                    stmt.executeUpdate();
                }
            }
            System.out.println("Chats par défaut ajoutés pour la communauté ID: " + communauteId);
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout des chats par défaut : " + e.getMessage());
        }
    }


}
