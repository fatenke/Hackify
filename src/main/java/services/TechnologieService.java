package services;

import models.Technologie;
import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TechnologieService  {

    Connection conn;

    public TechnologieService() {
        this.conn = MyConnection.getInstance().getConnection();
    }

    public void add(Technologie technologie) {
        String SQL = "INSERT INTO `technologies` (`nom_tech`, `type_tech`, `complexite`, `documentaire`, `compatibilite`) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, technologie.getNom_tech());
            pstmt.setString(2, technologie.getType_tech());
            pstmt.setString(3, technologie.getComplexite());
            pstmt.setString(4, technologie.getDocumentaire());
            pstmt.setString(5, technologie.getCompatibilite());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                technologie.setId_tech(rs.getInt(1));
            }
            System.out.println("Technologie ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la technologie : " + e.getMessage());
            throw new RuntimeException(e); // Propagate exception for handling
        }
    }

    public void update(Technologie technologie) {
        String SQL = "UPDATE technologies SET nom_tech = ?, type_tech = ?, complexite = ?, documentaire = ?, compatibilite = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, technologie.getNom_tech());
            pstmt.setString(2, technologie.getType_tech());
            pstmt.setString(3, technologie.getComplexite());
            pstmt.setString(4, technologie.getDocumentaire());
            pstmt.setString(5, technologie.getCompatibilite());
            pstmt.setInt(6, technologie.getId_tech());

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Technologie mise à jour avec succès !");
            } else {
                System.out.println("Aucune technologie trouvée avec l'ID : " + technologie.getId_tech());
                throw new SQLException("No technology found with ID: " + technologie.getId_tech());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la technologie : " + e.getMessage());
            throw new RuntimeException(e); // Propagate exception
        }
    }

    public void delete(int technologie) {
        String SQL = "DELETE FROM technologies WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, technologie);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Technologie supprimée avec succès !");
            } else {
                System.out.println("Aucune technologie trouvée avec l'ID : " + technologie);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la technologie : " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Technologie> getAll() {
        String req = "SELECT * FROM technologies";
        ArrayList<Technologie> technologies = new ArrayList<>();
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                Technologie t = new Technologie();
                t.setId_tech(rs.getInt("id"));
                t.setNom_tech(rs.getString("nom_tech"));
                t.setType_tech(rs.getString("type_tech"));
                t.setComplexite(rs.getString("complexite"));
                t.setDocumentaire(rs.getString("documentaire"));
                t.setCompatibilite(rs.getString("compatibilite"));
                technologies.add(t);
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des technologies : " + ex.getMessage());
            throw new RuntimeException(ex);
        }
        return technologies;
    }
    
    /**
     * Obtient les statistiques de compatibilité des technologies
     * @return Une liste de paires (compatibilité, nombre de technologies)
     */
    public List<CompatibilityStat> getCompatibilityStats() {
        String req = "SELECT compatibilite, COUNT(*) as count FROM technologies GROUP BY compatibilite ORDER BY count DESC";
        ArrayList<CompatibilityStat> stats = new ArrayList<>();
        try (Statement stm = conn.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                String compatibility = rs.getString("compatibilite");
                int count = rs.getInt("count");
                stats.add(new CompatibilityStat(compatibility, count));
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération des statistiques de compatibilité : " + ex.getMessage());
            throw new RuntimeException(ex);
        }
        return stats;
    }
    
    /**
     * Classe interne pour représenter les statistiques de compatibilité
     */
    public static class CompatibilityStat {
        private String compatibility;
        private int count;
        
        public CompatibilityStat(String compatibility, int count) {
            this.compatibility = compatibility;
            this.count = count;
        }
        
        public String getCompatibility() {
            return compatibility;
        }
        
        public int getCount() {
            return count;
        }
    }
}

