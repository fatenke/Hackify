package services;


import models.Vote;
import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoteService implements IService<Vote> {

    Connection conn = MyConnection.getInstance().getConnection();

    public VoteService() {
    }

    public void add(Vote vote) {

        String SQL = "insert into vote (idEvaluation, idVotant, idProjet, idHackathon, valeurVote,  date) values ('" + vote.getIdEvaluation() + "','" + vote.getIdVotant() + "','" + vote.getIdProjet() + "','" + vote.getIdHackathon() + "','" + vote.getValeurVote() + "','" + vote.getDate() + "')";
        Statement stmt = null;

        try {
            stmt = this.conn.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException var5) {
            SQLException e = var5;
            System.out.println(e.getMessage());
        }

    }

    public void update(Vote vote) {
        String SQL = "UPDATE vote SET idEvaluation = ?, idVotant = ?, idProjet = ?, idHackathon = ?, valeurVote = ?, date = ? WHERE id = ?";

        try {
            PreparedStatement pstmt = this.conn.prepareStatement(SQL);

            try {
                pstmt.setInt(1, vote.getIdEvaluation());
                pstmt.setInt(2, vote.getIdVotant());
                pstmt.setInt(3, vote.getIdProjet());
                pstmt.setInt(4, vote.getIdHackathon());
                pstmt.setFloat(5, vote.getValeurVote());
                pstmt.setString(6, vote.getDate());
                pstmt.setInt(7, vote.getId());
                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("✅ Vote updated successfully.");
                } else {
                    System.out.println("⚠️ No vote found with ID: " + vote.getId());
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
            System.out.println("❌ Error updating vote: " + e.getMessage());
        }

    }

    public void delete(Vote vote) {
        String SQL = "DELETE FROM vote WHERE id = ?";

        try {
            PreparedStatement pstmt = this.conn.prepareStatement(SQL);

            try {
                pstmt.setInt(1, vote.getId());
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("✅ Vote deleted successfully.");
                } else {
                    System.out.println("⚠️ No vote found with ID: " + vote.getId());
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
            System.out.println("❌ Error deleting vote: " + e.getMessage());
        }

    }

    public List<Vote> getAll() {
        String req = "SELECT * FROM `vote`";
        ArrayList<Vote> votes = new ArrayList();

        try {
            Statement stm = this.conn.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while(rs.next()) {
                Vote p = new Vote();
                p.setId(rs.getInt("id"));
                p.setIdEvaluation(rs.getInt("idEvaluation"));
                p.setIdVotant(rs.getInt("idVotant"));
                p.setIdProjet(rs.getInt("idProjet"));
                p.setIdHackathon(rs.getInt("idHackathon"));
                p.setValeurVote(rs.getFloat("valeurVote"));
                p.setDate(rs.getString("date"));
                votes.add(p);
            }
        } catch (SQLException var6) {
            SQLException ex = var6;
            System.out.println(ex.getMessage());
        }

        return votes;
    }
}
