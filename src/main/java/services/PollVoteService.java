package services;

import Interfaces.GlobalInterface;
import models.PollVote;
import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PollVoteService implements GlobalInterface<PollVote> {
    private final Connection conn;

    public PollVoteService() {
        this.conn = MyConnection.getInstance().getCnx();
        if (this.conn == null) {
            throw new IllegalStateException("Database connection is not established.");
        }
    }

    @Override
    public void add(PollVote vote) {
        String sql = "INSERT INTO votes (poll_id, option_id, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, vote.getPollId());
            stmt.setInt(2, vote.getOptionId());
            stmt.setInt(3, vote.getUserId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    vote.setId(rs.getInt(1));
                    System.out.println("Vote enregistré avec succès avec l'ID : " + vote.getId());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'enregistrement du vote : " + e.getMessage());
        }
    }

    @Override
    public void update(PollVote vote) {
        String sql = "UPDATE votes SET poll_id = ?, option_id = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vote.getPollId());
            stmt.setInt(2, vote.getOptionId());
            stmt.setInt(3, vote.getUserId());
            stmt.setInt(4, vote.getId());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Vote mis à jour avec succès !");
            } else {
                System.out.println("Aucun vote trouvé avec l'ID : " + vote.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du vote : " + e.getMessage());
        }
    }

    @Override
    public List<PollVote> getAll() {
        List<PollVote> votes = new ArrayList<>();
        String sql = "SELECT * FROM votes";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                votes.add(new PollVote(
                        rs.getInt("id"),
                        rs.getInt("poll_id"),
                        rs.getInt("option_id"),
                        rs.getInt("user_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des votes : " + e.getMessage());
        }
        return votes;
    }

    @Override
    public void delete(PollVote vote) {
        String sql = "DELETE FROM votes WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vote.getId());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Vote supprimé avec succès !");
            } else {
                System.out.println("Aucun vote trouvé avec l'ID : " + vote.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du vote : " + e.getMessage());
        }
    }

    public boolean hasUserVoted(int pollId, int userId) {
        String sql = "SELECT id FROM votes WHERE poll_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pollId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du vote de l'utilisateur : " + e.getMessage());
            return false;
        }
    }

    public void recordVote(int pollId, int optionId, int userId) {
        if (hasUserVoted(pollId, userId)) {
            System.out.println("L'utilisateur a déjà voté pour ce sondage.");
            return;
        }
        PollVote vote = new PollVote(pollId, optionId, userId);
        add(vote);
        new PollOptionService().incrementVoteCount(optionId);
    }

    public List<PollVote> getVotesByPollId(int pollId) {
        List<PollVote> votes = new ArrayList<>();
        String sql = "SELECT * FROM votes WHERE poll_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pollId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    votes.add(new PollVote(
                            rs.getInt("id"),
                            rs.getInt("poll_id"),
                            rs.getInt("option_id"),
                            rs.getInt("user_id")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des votes par sondage : " + e.getMessage());
        }
        return votes;
    }

    public void deleteVotesByPollId(int pollId) {
        String sql = "DELETE FROM votes WHERE poll_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pollId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression des votes par sondage : " + e.getMessage());
        }
    }
}