package services;

import Interfaces.GlobalInterface;
import models.Reaction;
import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactionService implements GlobalInterface<Reaction> {
    private Connection conn;

    public ReactionService() {
        conn = MyConnection.getInstance().getConnection();
        if (conn == null) {
            throw new IllegalStateException("Database connection is not established.");
        }
    }

    @Override
    public void add(Reaction reaction) {
        if (reaction == null || reaction.getEmoji() == null || reaction.getEmoji().isEmpty()) {
            throw new IllegalArgumentException("Reaction and emoji are required");
        }
        if (reaction.getEmoji().length() > 20) {
            throw new IllegalArgumentException("Invalid emoji format");
        }
        if (reaction.getMessageId() == 0 || reaction.getUserId() == 0) {
            throw new IllegalArgumentException("Message ID and user ID are required");
        }
        if (reaction.getCreatedAt() == null) {
            reaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }

        try {
            // Check for existing reaction
            String checkSql = "SELECT COUNT(*) FROM reaction WHERE user_id = ? AND message_id = ? AND emoji = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, reaction.getUserId());
            checkStmt.setInt(2, reaction.getMessageId());
            checkStmt.setString(3, reaction.getEmoji());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                throw new IllegalStateException("Reaction already exists");
            }

            String sql = "INSERT INTO reaction (user_id, message_id, emoji, created_at) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, reaction.getUserId());
            pstmt.setInt(2, reaction.getMessageId());
            pstmt.setString(3, reaction.getEmoji());
            pstmt.setTimestamp(4, reaction.getCreatedAt());
            pstmt.executeUpdate();

            // Set generated ID
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                reaction.setId(generatedKeys.getInt(1));
            }
            System.out.println("Reaction added successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Error adding reaction: " + e.getMessage());
        }
    }

    @Override
    public void update(Reaction reaction) {
        if (reaction == null || reaction.getEmoji() == null || reaction.getEmoji().isEmpty()) {
            throw new IllegalArgumentException("Reaction and emoji are required");
        }
        if (reaction.getEmoji().length() > 20) {
            throw new IllegalArgumentException("Invalid emoji format");
        }

        String sql = "UPDATE reaction SET emoji = ?, created_at = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reaction.getEmoji());
            pstmt.setTimestamp(2, reaction.getCreatedAt());
            pstmt.setInt(3, reaction.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Reaction not found");
            }
            System.out.println("Reaction updated successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating reaction: " + e.getMessage());
        }
    }

    @Override
    public List<Reaction> getAll() {
        List<Reaction> reactions = new ArrayList<>();
        String sql = "SELECT * FROM reaction";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reaction reaction = new Reaction();
                reaction.setId(rs.getInt("id"));
                reaction.setUserId(rs.getInt("user_id"));
                reaction.setMessageId(rs.getInt("message_id"));
                reaction.setEmoji(rs.getString("emoji"));
                reaction.setCreatedAt(rs.getTimestamp("created_at"));
                reactions.add(reaction);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reactions: " + e.getMessage());
        }
        return reactions;
    }

    @Override
    public void delete(Reaction reaction) {
        if (reaction == null) {
            throw new IllegalArgumentException("Reaction is required");
        }

        String sql = "DELETE FROM reaction WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reaction.getId());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Reaction not found");
            }
            System.out.println("Reaction deleted successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reaction: " + e.getMessage());
        }
    }

    public Map<String, Long> getReactionCounts(int messageId) {
        Map<String, Long> counts = new HashMap<>();
        String sql = "SELECT emoji, COUNT(*) as count FROM reaction WHERE message_id = ? GROUP BY emoji";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                counts.put(rs.getString("emoji"), rs.getLong("count"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reaction counts: " + e.getMessage());
        }
        return counts;
    }
}