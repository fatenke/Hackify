

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

        String SQL = "insert into evaluation (noteTech, noteInnov, date,idJury, idHackathon,idProjet) values ('" + evaluation.getNoteTech() + "','" + evaluation.getNoteInnov() + "','" + evaluation.getDate() + "','" + evaluation.getIdJury() + "','" + evaluation.getIdHackathon() + "','" + evaluation.getIdProjet() + "')";
        Statement stmt = null;

        try {
            stmt = this.conn.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException var5) {
            SQLException e = var5;
            System.out.println(e.getMessage());
        }

    }

    public void update(Evaluation evaluation) {
        String SQL = "UPDATE evaluation SET noteTech = ?, noteInnov = ?, date = ?, idJury = ?, idHackathon = ?, idProjet = ? WHERE id = ?";

        try {
            PreparedStatement pstmt = this.conn.prepareStatement(SQL);

            try {
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
            } catch (Throwable var7) {
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException var8) {
            SQLException e = var8;
            System.out.println("❌ Error updating evaluation: " + e.getMessage());
        }

    }

    public void delete(Evaluation evaluation) {
        String SQL = "DELETE FROM evaluation WHERE id = ?";

        try {
            PreparedStatement pstmt = this.conn.prepareStatement(SQL);

            try {
                pstmt.setInt(1, evaluation.getId());
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("✅ Evaluation deleted successfully.");
                } else {
                    System.out.println("⚠️ No evaluation found with ID: " + evaluation.getId());
                }
            } catch (Throwable var7) {
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException var8) {
            SQLException e = var8;
            System.out.println("❌ Error deleting evaluation: " + e.getMessage());
        }

    }

    public List<Evaluation> getAll() {
        String req = "SELECT * FROM `evaluation`";
        ArrayList<Evaluation> evaluations = new ArrayList();

        try {
            Statement stm = this.conn.createStatement();
            ResultSet rs = stm.executeQuery(req);

            while(rs.next()) {
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
        } catch (SQLException var6) {
            SQLException ex = var6;
            System.out.println(ex.getMessage());
        }

        return evaluations;
    }
}
