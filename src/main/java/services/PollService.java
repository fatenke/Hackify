package services;

import Interfaces.GlobalInterface;
import models.Poll;
import models.PollOption;
import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PollService implements GlobalInterface<Poll> {
    private final Connection conn;
    private final PollOptionService optionService;

    public PollService() {
        this.conn = MyConnection.getInstance().getCnx();
        if (this.conn == null) {
            throw new IllegalStateException("Database connection is not established.");
        }
        this.optionService = new PollOptionService();
    }

    @Override
    public void add(Poll poll) {
        String sql = "INSERT INTO poll (chat_id, question, created_at, is_closed) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, poll.getChatId());
            stmt.setString(2, poll.getQuestion());
            stmt.setTimestamp(3, poll.getCreatedAt());
            stmt.setBoolean(4, poll.isClosed());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int pollId = rs.getInt(1);
                    poll.setId(pollId);

                    // Add poll options
                    if (poll.getOptions() != null && !poll.getOptions().isEmpty()) {
                        for (PollOption option : poll.getOptions()) {
                            option.setPollId(pollId);
                            optionService.add(option);
                        }
                    }
                    System.out.println("Sondage créé avec succès avec l'ID : " + pollId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création du sondage : " + e.getMessage());
        }
    }

    @Override
    public void update(Poll poll) {
        String sql = "UPDATE poll SET chat_id = ?, question = ?, created_at = ?, is_closed = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poll.getChatId());
            stmt.setString(2, poll.getQuestion());
            stmt.setTimestamp(3, poll.getCreatedAt());
            stmt.setBoolean(4, poll.isClosed());
            stmt.setInt(5, poll.getId());
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Delete existing options and add new ones
                optionService.deleteOptionsByPollId(poll.getId());
                if (poll.getOptions() != null && !poll.getOptions().isEmpty()) {
                    for (PollOption option : poll.getOptions()) {
                        option.setPollId(poll.getId());
                        optionService.add(option);
                    }
                }
                System.out.println("Sondage mis à jour avec succès !");
            } else {
                System.out.println("Aucun sondage trouvé avec l'ID : " + poll.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du sondage : " + e.getMessage());
        }
    }

    @Override
    public List<Poll> getAll() {
        List<Poll> polls = new ArrayList<>();
        String sql = "SELECT * FROM poll";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int pollId = rs.getInt("id");
                polls.add(new Poll(
                        pollId,
                        rs.getInt("chat_id"),
                        rs.getString("question"),
                        optionService.getOptionsByPollId(pollId),
                        rs.getBoolean("is_closed"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des sondages : " + e.getMessage());
        }
        return polls;
    }

    @Override
    public void delete(Poll poll) {
        // Delete related votes and options first
        new PollVoteService().deleteVotesByPollId(poll.getId());
        optionService.deleteOptionsByPollId(poll.getId());

        String sql = "DELETE FROM poll WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poll.getId());
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Sondage supprimé avec succès !");
            } else {
                System.out.println("Aucun sondage trouvé avec l'ID : " + poll.getId());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du sondage : " + e.getMessage());
        }
    }

    public void closePoll(int pollId) {
        String sql = "UPDATE poll SET is_closed = TRUE WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pollId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Sondage fermé avec succès !");
            } else {
                System.out.println("Aucun sondage trouvé avec l'ID : " + pollId);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la fermeture du sondage : " + e.getMessage());
        }
    }

    public boolean isPollClosed(int pollId) {
        String sql = "SELECT is_closed FROM poll WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pollId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("is_closed");
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification de l'état du sondage : " + e.getMessage());
        }
        return true; // Default to closed if error occurs
    }

    public Poll getById(int pollId) {
        String sql = "SELECT * FROM poll WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pollId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Poll(
                            rs.getInt("id"),
                            rs.getInt("chat_id"),
                            rs.getString("question"),
                            optionService.getOptionsByPollId(pollId),
                            rs.getBoolean("is_closed"),
                            rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du sondage : " + e.getMessage());
        }
        return null;
    }

    public List<Poll> getPollsByChatId(int chatId) {
        List<Poll> polls = new ArrayList<>();
        String sql = "SELECT * FROM poll WHERE chat_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int pollId = rs.getInt("id");
                    polls.add(new Poll(
                            pollId,
                            rs.getInt("chat_id"),
                            rs.getString("question"),
                            optionService.getOptionsByPollId(pollId),
                            rs.getBoolean("is_closed"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des sondages par chat : " + e.getMessage());
        }
        return polls;
    }
}