package services;

import models.Technologie;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnologieService implements IService<Technologie> {

    Connection conn;

    public TechnologieService() {
        this.conn = DBConnection.getInstance().getConn();
    }
    @Override
    public void add(Technologie technologie) {
        String SQL = "INSERT INTO `technologie` (`nom_tech`, `type_tech`, `complexite`, `documentaire`, `compatibilite`) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, technologie.getNom_tech());
            pstmt.setString(2, technologie.getType_tech());
            pstmt.setString(3, technologie.getComplexite());
            pstmt.setString(4, technologie.getDocumentaire());
            pstmt.setString(5, technologie.getCompatibilite());
            pstmt.executeUpdate();
            System.out.println("Technologie ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout de la technologie : " + e.getMessage());
        }
    }

    @Override
    public void update(Technologie technologie) {
        String SQL = "UPDATE technologie SET nom_tech = '" + technologie.getNom_tech() +
                "', type_tech = '" + technologie.getType_tech() +
                "', complexite = '" + technologie.getComplexite() +
                "', documentaire = '" + technologie.getDocumentaire() +
                "', compatibilite = '" + technologie.getCompatibilite() +
                "' WHERE id_tech = " + technologie.getId_tech();

        try (Statement stmt = conn.createStatement()) {
            int rowsUpdated = stmt.executeUpdate(SQL);
            if (rowsUpdated > 0) {
                System.out.println("Technologie mise à jour avec succès !");
            } else {
                System.out.println("Aucune technologie trouvée avec l'ID : " + technologie.getId_tech());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la technologie : " + e.getMessage());
        }
    }

    @Override
    public void delete(Technologie technologie) {
        String SQL = "DELETE FROM technologie WHERE id_tech = " + technologie.getId_tech();

        try (Statement stmt = conn.createStatement()) {
            int rowsDeleted = stmt.executeUpdate(SQL);
            if (rowsDeleted > 0) {
                System.out.println("Technologie supprimée avec succès !");
            } else {
                System.out.println("Aucune technologie trouvée avec l'ID : " + technologie.getId_tech());
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la technologie : " + e.getMessage());
        }
    }

    @Override
    public List<Technologie> getAll() {
        String req = "SELECT * FROM technologie";
        ArrayList<Technologie> technologies = new ArrayList<>();
        Statement stm;
        try {
            stm = this.conn.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Technologie t = new Technologie();
                t.setId_tech(rs.getInt("id_tech"));
                t.setNom_tech(rs.getString("nom_tech"));
                t.setType_tech(rs.getString("type_tech"));
                t.setComplexite(rs.getString("complexite"));
                t.setDocumentaire(rs.getString("documentaire"));
                t.setCompatibilite(rs.getString("compatibilite")); // Added to retrieve compatibilite from the database

                // Ajouter la technologie à la liste des technologies
                technologies.add(t);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération des technologies : " + ex.getMessage());
        }
        return technologies;
    }
}