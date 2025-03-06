package services;

import util.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PollOptionService {
    private final Connection conn = MyConnection.getInstance().getCnx();

    public int createOption(int pollId, String text) {
        String sql = "INSERT INTO poll_options (poll_id, text, vote_count) VALUES (?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pollId);
            ps.setString(2, text);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void incrementVoteCount(int optionId) {
        String sql = "UPDATE poll_options SET vote_count = vote_count + 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, optionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getVoteCount(int optionId) {
        String sql = "SELECT vote_count FROM poll_options WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, optionId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("vote_count") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}