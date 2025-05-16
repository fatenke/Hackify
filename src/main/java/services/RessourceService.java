package services;
import Interfaces.IService;
import models.Ressource;
import util.MyConnection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
public  class RessourceService implements IService<Ressource> {
    private final Connection conn;

    public RessourceService() {
        this.conn = MyConnection.getInstance().getConnection();

    }
    public  boolean validateRessource(Ressource ressource) {
        if (ressource.getTitre() == null || ressource.getTitre().trim().isEmpty()) {
            System.out.println("❌ Erreur : Le titre de la ressource est obligatoire !");
            return false;
        }
        return true;
    }

    @Override
    public void add(Ressource ressource) {
        String SQL = "INSERT INTO ressources (titre, type, description, date_ajout, valide) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = conn.prepareStatement(SQL);
            pst.setString(1, ressource.getTitre());
            pst.setString(2, ressource.getType());
            pst.setString(3, ressource.getDescription());
            pst.setDate(4, new java.sql.Date(ressource.getDateAjout().getTime())); // Conversion
            pst.setBoolean(5, ressource.isValide());

            pst.executeUpdate();
            System.out.println("✅ Ressource ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }



    @Override
    public void delete(Ressource ressource) {
        String SQL = "DELETE FROM ressources WHERE id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(SQL);
            pst.setInt(1, ressource.getId());

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Ressource supprimée avec succès !");
            } else {
                System.out.println("❌ Aucune ressource trouvée avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
        System.out.println("Tentative de suppression de : " + ressource.getTitre());
        try {
            String query = "DELETE FROM ressources WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, ressource.getId());
            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Ressource supprimée avec succès !");
            } else {
                System.out.println("Échec de la suppression !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void update(Ressource ressource) {
        System.out.println("Tentative de modification de : " + ressource.getTitre());
        try {
            String query = "UPDATE ressources SET titre = ?, type = ?, description = ?, dateAjout = ?, valide = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(query);

            pst.setString(1, ressource.getTitre());
            pst.setString(2, ressource.getType());
            pst.setString(3, ressource.getDescription());

            // ✅ Conversion de java.util.Date en java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(ressource.getDateAjout().getTime());
            pst.setDate(4, sqlDate);

            pst.setBoolean(5, ressource.isValide());
            pst.setInt(6, ressource.getId());

            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Ressource modifiée avec succès !");
            } else {
                System.out.println("Échec de la modification !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public List<Ressource> getAll() {
        List<Ressource> ressources = new ArrayList<>();
        String checkTableSQL = "SHOW TABLES LIKE 'ressources'"; // Vérifier l'existence de la table
        String SQL = "SELECT * FROM ressources";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rsCheck = stmt.executeQuery(checkTableSQL);

            if (!rsCheck.next()) {
                System.out.println("❌ Erreur : La table 'ressource' n'existe pas. Vérifiez votre base de données !");
                return ressources;
            }

            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Ressource r = new Ressource();
                r.setId(rs.getInt("id"));
                r.setTitre(rs.getString("titre"));
                r.setType(rs.getString("type"));
                r.setDescription(rs.getString("description"));
                r.setDateAjout(rs.getDate("date_ajout"));
                r.setValide(rs.getBoolean("valide"));

                ressources.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des ressources : " + e.getMessage());
        }
        return ressources;
    }


   /* public Ressource getById(int id) {
        Ressource r = null;
        String SQL = "SELECT * FROM ressource WHERE id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(SQL);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                r = new Ressource();
                r.setId(rs.getInt("id"));
                r.setTitre(rs.getString("titre"));
                r.setType(rs.getString("type"));
                r.setDescription(rs.getString("description"));
                r.setDateAjout(rs.getDate("date_ajout"));
                r.setValide(rs.getBoolean("valide"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }
        return r;
    }*/
}

