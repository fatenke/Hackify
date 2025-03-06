package services;
import Interfaces.GlobalInterface;
import models.Projet;
import util.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProjetService implements GlobalInterface<Projet> {

    public final Connection cnx;

    public ProjetService() {
        this.cnx = MyConnection.getInstance().getCnx();
    }

    @Override
    public void add(Projet projet) {
        String SQL = "INSERT INTO `projet`( `nom`, `statut`, `priorite`, `description`, `ressource`) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = cnx.prepareStatement(SQL)) {
            pstmt.setString(1, projet.getNom());
            pstmt.setString(2, projet.getStatut());
            pstmt.setString(3, projet.getPriorite());
            pstmt.setString(4, projet.getDescription());
            pstmt.setString(5, projet.getRessource());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Projet projet) {
        String SQL = "UPDATE projet SET nom = ?, statut = ?, priorite = ?, description = ?, ressource = ? WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(SQL)) {
            pstmt.setString(1, projet.getNom());
            pstmt.setString(2, projet.getStatut());
            pstmt.setString(3, projet.getPriorite());
            pstmt.setString(4, projet.getDescription());
            pstmt.setString(5, projet.getRessource());
            pstmt.setInt(6, projet.getId_pr());
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Projet mis à jour avec succès !");
            } else {
                System.out.println("Aucun projet trouvé avec l'ID : " + projet.getId_pr());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du projet : " + e.getMessage());
            throw new RuntimeException("Erreur SQL lors de la mise à jour", e); // Throw exception for controller to catch
        }
    }

    @Override
    public void delete(Projet projet) {
        String SQL = "DELETE FROM `projet` WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(SQL)) {
            pstmt.setInt(1, projet.getId_pr());
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Projet supprimé avec succès !");
            } else {
                System.out.println("Aucun projet trouvé avec l'ID : " + projet.getId_pr());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du projet : " + e.getMessage());
            throw new RuntimeException("Erreur SQL lors de la suppression", e); // Throw exception for better handling
        }
    }

    @Override
    public List<Projet> getAll() {
        ArrayList<Projet> projets = new ArrayList<>();
        String req = "SELECT * FROM `projet`";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            while (rs.next()) {
                Projet p = new Projet();
                p.setId_pr(rs.getInt("id")); // Assuming 'id' is the primary key column
                p.setNom(rs.getString("nom"));
                p.setStatut(rs.getString("statut"));
                p.setPriorite(rs.getString("priorite"));
                p.setDescription(rs.getString("description"));
                p.setRessource(rs.getString("ressource")); // Added ressource retrieval

                projets.add(p);
            }

            rs.close();
            stm.close();

        } catch (SQLException ex) {
            System.out.println("Erreur SQL: " + ex.getMessage());
            throw new RuntimeException("Erreur SQL lors de la récupération des projets", ex);
        }

        return projets;
    }
}