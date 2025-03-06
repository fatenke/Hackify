package services;

import Interfaces.GlobalInterface;
import com.mysql.cj.jdbc.JdbcConnection;
import models.Communaute;
import util.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommunauteService implements GlobalInterface<Communaute> {
    private ChatService chatService;
    Connection conn ;
    public CommunauteService() {
        this.conn = MyConnection.getInstance().getCnx() ;
        this.chatService = new ChatService(); // Initialize ChatService

    }


    @Override
    public void add(Communaute communaute) {
        String sql = "INSERT INTO communaute (id_hackathon, nom, description, date_creation) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, communaute.getHackathonId());  // Set hackathon_id
            stmt.setString(2, communaute.getNom());       // Set name
            stmt.setString(3, communaute.getDescription());// Set description
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Current date

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int communauteId = generatedKeys.getInt(1);
                        System.out.println("Communaute créée avec ID: " + communauteId);

                        // Ajouter les chats par défaut
                        chatService.addDefaultChats(communauteId);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void update(Communaute communaute) {
            String sql = "UPDATE communaute SET   nom = ?, description = ? WHERE id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, communaute.getNom());
                pstmt.setString(2, communaute.getDescription());
                pstmt.setInt(3, communaute.getId());

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Communaute updated successfully!");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

    }

    @Override
    public List<Communaute> getAll() {
        List<Communaute> communautes = new ArrayList<>();
        String sql = "SELECT * FROM communaute";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Communaute c = new Communaute(
                        rs.getInt("id"),
                        rs.getInt("id_hackathon"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getTimestamp("date_creation")
                );
                communautes.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return communautes;
    }

    @Override
    public void delete(Communaute communaute) {
        // Step 1: Delete all chats related to this community
        chatService.deleteAllByCommunityId(communaute.getId());

        // Step 2: Delete the community itself
        String sql = "DELETE FROM communaute WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, communaute.getId());
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Communaute deleted successfully!");
            } else {
                System.out.println("No Communaute found with ID: " + communaute.getId());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



}
