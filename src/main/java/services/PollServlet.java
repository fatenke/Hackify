package services;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/poll")
public class PollServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        try (Connection conn = getDatabaseConnection()) {
            if ("create".equals(action)) {
                createPoll(req, conn);
            } else if ("vote".equals(action)) {
                castVote(req, conn);
            }
            resp.sendRedirect("polls.jsp?chatId=" + req.getParameter("chatId")); // Redirect to poll page
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void createPoll(HttpServletRequest req, Connection conn) throws SQLException {
        int chatId = Integer.parseInt(req.getParameter("chatId"));
        String question = req.getParameter("question");
        String[] options = req.getParameterValues("options");

        // Insert poll
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO polls (chat_id, question) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, chatId);
        ps.setString(2, question);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int pollId = rs.next() ? rs.getInt(1) : -1;

        // Insert options
        ps = conn.prepareStatement("INSERT INTO poll_options (poll_id, text) VALUES (?, ?)");
        for (String option : options) {
            ps.setInt(1, pollId);
            ps.setString(2, option);
            ps.addBatch();
        }
        ps.executeBatch();
    }

    private void castVote(HttpServletRequest req, Connection conn) throws SQLException {
        int pollId = Integer.parseInt(req.getParameter("pollId"));
        int optionId = Integer.parseInt(req.getParameter("optionId"));
        int userId = getUserIdFromSession(req); // Replace with your auth logic

        // Check if user already voted
        PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM votes WHERE poll_id = ? AND user_id = ?");
        ps.setInt(1, pollId);
        ps.setInt(2, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            throw new IllegalStateException("User already voted.");
        }

        // Record vote
        ps = conn.prepareStatement("INSERT INTO votes (poll_id, option_id, user_id) VALUES (?, ?, ?)");
        ps.setInt(1, pollId);
        ps.setInt(2, optionId);
        ps.setInt(3, userId);
        ps.executeUpdate();

        // Update vote count
        ps = conn.prepareStatement("UPDATE poll_options SET vote_count = vote_count + 1 WHERE id = ?");
        ps.setInt(1, optionId);
        ps.executeUpdate();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Optional: Fetch polls for display (handled in JSP via servlet forwarding if needed)
        resp.sendRedirect("polls.jsp?chatId=" + req.getParameter("chatId"));
    }

    private Connection getDatabaseConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hackify?useSSL=false&serverTimezone=UTC";
        String user = "your_username";
        String password = "your_password";
        return DriverManager.getConnection(url, user, password);
    }

    private int getUserIdFromSession(HttpServletRequest req) {
        // Replace with your authentication logic (e.g., session-based user ID)
        return 1; // Dummy user ID for testing
    }
}