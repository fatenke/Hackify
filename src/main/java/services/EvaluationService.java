package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Evaluation;
import util.DBConnection;

public class EvaluationService implements IService<Evaluation> {
    Connection conn = DBConnection.getInstance().getConn();

    public EvaluationService() {
    }

    public void add(Evaluation evaluation) {
        String SQL = "INSERT INTO evaluation (noteTech, noteInnov, date, idJury, idHackathon, idProjet) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setFloat(1, evaluation.getNoteTech());
            pstmt.setFloat(2, evaluation.getNoteInnov());
            pstmt.setString(3, evaluation.getDate());
            pstmt.setInt(4, evaluation.getIdJury());
            pstmt.setInt(5, evaluation.getIdHackathon());
            pstmt.setInt(6, evaluation.getIdProjet());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error adding evaluation: " + e.getMessage());
        }
    }

    public List<Integer> getHackathonIds() throws SQLException {
        String query = "SELECT idHackathon FROM hackathon";
        List<Integer> hackathonIds = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                hackathonIds.add(rs.getInt("idHackathon"));
            }
        }
        return hackathonIds;
    }

    public List<Integer> getProjectsByHackathonId(int hackathonId) throws SQLException {
        String query = "SELECT idProjet FROM projet WHERE idHackathon = ?";
        List<Integer> projectIds = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, hackathonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    projectIds.add(rs.getInt("idProjet"));
                }
            }
        }
        return projectIds;
    }

    public void update(Evaluation evaluation) {
        String SQL = "UPDATE evaluation SET noteTech = ?, noteInnov = ?, date = ?, idJury = ?, idHackathon = ?, idProjet = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setFloat(1, evaluation.getNoteTech());
            pstmt.setFloat(2, evaluation.getNoteInnov());
            pstmt.setString(3, evaluation.getDate());
            pstmt.setInt(4, evaluation.getIdJury());
            pstmt.setInt(5, evaluation.getIdHackathon());
            pstmt.setInt(6, evaluation.getIdProjet());
            pstmt.setInt(7, evaluation.getId());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Evaluation updated successfully.");
            } else {
                System.out.println("⚠️ No evaluation found with ID: " + evaluation.getId());
            }
        } catch (SQLException e) {
            System.out.println("❌ Error updating evaluation: " + e.getMessage());
        }
    }

    public void delete(Evaluation evaluation) {
        String SQL = "DELETE FROM evaluation WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, evaluation.getId());
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Evaluation deleted successfully.");
            } else {
                System.out.println("⚠️ No evaluation found with ID: " + evaluation.getId());
            }
        } catch (SQLException e) {
            System.out.println("❌ Error deleting evaluation: " + e.getMessage());
        }
    }

    public List<Evaluation> getAll() {
        String req = "SELECT * FROM `evaluation`";
        List<Evaluation> evaluations = new ArrayList<>();
        try (Statement stm = conn.createStatement(); ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                Evaluation p = new Evaluation();
                p.setId(rs.getInt("id"));
                p.setIdJury(rs.getInt("idJury"));
                p.setIdHackathon(rs.getInt("idHackathon"));
                p.setIdProjet(rs.getInt("idProjet"));
                p.setNoteTech(rs.getFloat("NoteTech"));
                p.setNoteInnov(rs.getFloat("NoteInnov"));
                p.setDate(rs.getString("date"));
                evaluations.add(p);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return evaluations;
    }
    public List<Integer> getEvaluationIds() throws SQLException {
        String query = "SELECT id FROM evaluation"; // Assuming the evaluation table has a column 'id' for evaluation ID
        List<Integer> evaluationIds = new ArrayList<>();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                evaluationIds.add(rs.getInt("id")); // 'id' should match your actual column name for evaluation IDs
            }
        }
        return evaluationIds;
    }


}
