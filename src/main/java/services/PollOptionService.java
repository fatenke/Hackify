package services;

import Interfaces.GlobalInterface;
import models.PollOption;
import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PollOptionService implements GlobalInterface<PollOption> {
    private final Connection conn;

    public PollOptionService() {
        this.conn = MyConnection.getInstance().getCnx();
        if (this.conn == null) {
            throw new IllegalStateException("Database connection is not established.");
        }
    }

    @Override
    public void add(PollOption option) {
        String sql = "INSERT INTO poll_options (poll_id, text, vote_count) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, option.getPollId());
            stmt.setString(2, option.getText());
            stmt.setInt(3, option.getVoteCount());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    option.setId(rs.getInt(1));
                    System.out.println("Option de sondage créée avec succès avec l'ID : " + option.getId());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création de l'option de sondage : " + e.getMessage());
        }
    }

    @Override
    public void update(PollOption option) {
        String sql = "UPDATE poll_options SET poll_id = ?, text = ?, vote_count = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, option.getPollId());
            stmt.setString(2, option.getText());
            stmt.setInt(3, option.getVoteCount());
            stmt.setInt(4, option.getId());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Option de sondage mise à jour avec succès !");
            } else {
                System.out.println("Aucune option trouvée avec l'ID : " + option.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de l'option de sondage : " + e.getMessage());
        }
    }

    @Override
    public List<PollOption> getAll() {
        List<PollOption> options = new ArrayList<>();
        String sql = "SELECT * FROM poll_options";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                options.add(new PollOption(
                        rs.getInt("id"),
                        rs.getInt("poll_id"),
                        rs.getString("text"),
                        rs.getInt("vote_count")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des options de sondage : " + e.getMessage());
        }
        return options;
    }

    @Override
    public void delete(PollOption option) {
        String sql = "DELETE FROM poll_options WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, option.getId());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Option de sondage supprimée avec succès !");
            } else {
                System.out.println("Aucune option trouvée avec l'ID : " + option.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de l'option de sondage : " + e.getMessage());
        }
    }

    public void incrementVoteCount(int optionId) {
        String sql = "UPDATE poll_options SET vote_count = vote_count + 1 WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, optionId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Compte de votes incrémenté pour l'option ID : " + optionId);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'incrémentation du compte de votes : " + e.getMessage());
        }
    }

    public int getVoteCount(int optionId) {
        String sql = "SELECT vote_count FROM poll_options WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, optionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("vote_count");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du compte de votes : " + e.getMessage());
        }
        return 0;
    }

    public List<PollOption> getOptionsByPollId(int pollId) {
        List<PollOption> options = new ArrayList<>();
        String sql = "SELECT * FROM poll_options WHERE poll_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pollId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    options.add(new PollOption(
                            rs.getInt("id"),
                            rs.getInt("poll_id"),
                            rs.getString("text"),
                            rs.getInt("vote_count")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des options par sondage : " + e.getMessage());
        }
        return options;
    }

    public void deleteOptionsByPollId(int pollId) {
        String sql = "DELETE FROM poll_options WHERE poll_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pollId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression des options par sondage : " + e.getMessage());
        }
    }
}