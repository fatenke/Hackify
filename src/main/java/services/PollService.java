package services;

import util.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Statement;

public class PollService {
    private final Connection conn = MyConnection.getInstance().getCnx();

    public int createPoll(int chatId, String question, Timestamp createdAt) {
        String sql = "INSERT INTO polls (chat_id, question, created_at, is_closed) VALUES (?, ?, ?, FALSE)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, chatId);
            ps.setString(2, question);
            ps.setTimestamp(3, createdAt);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void closePoll(int pollId) {
        String sql = "UPDATE polls SET is_closed = TRUE WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pollId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePoll(int pollId) {
        String sql = "DELETE FROM polls WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pollId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPollClosed(int pollId) {
        String sql = "SELECT is_closed FROM polls WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pollId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getBoolean("is_closed");
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // Default to closed if error occurs
        }
    }
}