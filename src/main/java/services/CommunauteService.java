package services;

import Interfaces.GlobalInterface;
import models.Communaute;
import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommunauteService implements GlobalInterface<Communaute> {
    private ChatService chatService;
    private  Connection conn;

    public CommunauteService() {
        this.conn = MyConnection.getInstance().getConnection();
        if (conn == null) {
            throw new IllegalStateException("Connexion à la base de données non établie.");
        }
        this.chatService = new ChatService();
    }

    @Override
    public void add(Communaute communaute) {
        String sql = "INSERT INTO communaute (id_hackathon, nom, description, date_creation, is_active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, communaute.getIdHackathon());
            stmt.setString(2, communaute.getNom());
            stmt.setString(3, communaute.getDescription());
            stmt.setTimestamp(4, communaute.getDateCreation());
            stmt.setBoolean(5, communaute.isActive());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int communauteId = generatedKeys.getInt(1);
                        communaute.setId(communauteId);
                        System.out.println("Communauté créée avec ID : " + communauteId);
                        chatService.addDefaultChats(communauteId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de la communauté : " + e.getMessage());
        }
    }

    @Override
    public void update(Communaute communaute) {
        String sql = "UPDATE communaute SET id_hackathon = ?, nom = ?, description = ?, date_creation = ?, is_active = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, communaute.getIdHackathon());
            stmt.setString(2, communaute.getNom());
            stmt.setString(3, communaute.getDescription());
            stmt.setTimestamp(4, communaute.getDateCreation());
            stmt.setBoolean(5, communaute.isActive());
            stmt.setInt(6, communaute.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("Aucune communauté trouvée avec l'ID : " + communaute.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la communauté : " + e.getMessage());
        }
    }

    @Override
    public List<Communaute> getAll() {
        List<Communaute> communautes = new ArrayList<>();
        String sql = "SELECT * FROM communaute";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Communaute communaute = new Communaute(
                        rs.getInt("id"),
                        rs.getInt("id_hackathon"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getTimestamp("date_creation"),
                        rs.getBoolean("is_active")
                );
                communautes.add(communaute);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des communautés : " + e.getMessage());
        }
        return communautes;
    }

    @Override
    public void delete(Communaute communaute) {
        try {
            conn.setAutoCommit(false); // Start transaction
            chatService.deleteAllByCommunityId(communaute.getId());
            String sql = "DELETE FROM communaute WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, communaute.getId());
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted == 0) {
                    throw new SQLException("Aucune communauté trouvée avec l'ID : " + communaute.getId());
                }
            }
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            try {
                conn.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Erreur lors de l'annulation de la transaction : " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Erreur lors de la suppression de la communauté : " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la réinitialisation de l'auto-commit : " + e.getMessage());
            }
        }
    }

    public Communaute getById(int id) {
        String sql = "SELECT * FROM communaute WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Communaute(
                            rs.getInt("id"),
                            rs.getInt("id_hackathon"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getTimestamp("date_creation"),
                            rs.getBoolean("is_active")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la communauté : " + e.getMessage());
        }
        return null;
    }
}