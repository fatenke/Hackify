package services;

import Interfaces.GlobalInterface;
import models.Communaute;
import models.Hackathon;

import util.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HackathonService implements GlobalInterface<Hackathon> {

    private final Connection connection;
    private final CommunauteService commuService = new CommunauteService() ;




    public HackathonService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public void add(Hackathon hackathon) {
        String req = "INSERT INTO `hackathon`(`id_organisateur`, `nom_hackathon`, `description`, `date_debut`, `date_fin`, `lieu`, `theme`, `max_participants`) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, hackathon.getId_organisateur());
            statement.setString(2, hackathon.getNom_hackathon());
            statement.setString(3, hackathon.getDescription());
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(hackathon.getDate_debut()));
            statement.setTimestamp(5, java.sql.Timestamp.valueOf(hackathon.getDate_fin()));
            statement.setString(6, hackathon.getLieu());
            statement.setString(7, hackathon.getTheme());
            statement.setInt(8, hackathon.getMax_participants());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de l'insertion du hackathon.");
            }

            // Query the ID afterward
            String queryId = "SELECT id_hackathon FROM hackathon WHERE nom_hackathon = ? AND id_organisateur = ? AND date_debut = ?";
            try (PreparedStatement queryStmt = connection.prepareStatement(queryId)) {
                queryStmt.setString(1, hackathon.getNom_hackathon());
                queryStmt.setInt(2, hackathon.getId_organisateur());
                queryStmt.setTimestamp(3, java.sql.Timestamp.valueOf(hackathon.getDate_debut()));
                ResultSet rs = queryStmt.executeQuery();
                if (rs.next()) {
                    int hackathonId = rs.getInt("id_hackathon");
                    hackathon.setId_hackathon(hackathonId);
                    System.out.println("Hackathon ajouté avec succès, ID: " + hackathonId);

                    // Create Communaute
                    Communaute communaute = new Communaute();
                    communaute.setIdHackathon(hackathonId);
                    communaute.setNom(hackathon.getNom_hackathon());
                    communaute.setDescription(hackathon.getDescription());
                    communaute.setDateCreation(new Timestamp(System.currentTimeMillis()));
                    communaute.setActive(true);
                    commuService.add(communaute);
                    System.out.println("Communauté associée créée pour le hackathon ID: " + hackathonId);
                } else {
                    throw new SQLException("Échec de la récupération de l'ID du hackathon.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du hackathon: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Hackathon hackathon) {
        String req = "UPDATE `hackathon` SET `nom_hackathon`=?, `description`=?, `date_debut`=?, `date_fin`=?, `lieu`=?, `theme`=?, `max_participants`=? WHERE `id_hackathon`=?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, hackathon.getNom_hackathon());
            statement.setString(2, hackathon.getDescription());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(hackathon.getDate_debut()));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(hackathon.getDate_fin()));
            statement.setString(5, hackathon.getLieu());
            statement.setString(6, hackathon.getTheme());
            statement.setInt(7, hackathon.getMax_participants());
            statement.setInt(8, hackathon.getId_hackathon());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Hackathon mis à jour avec succès !");
            } else {
                System.out.println("Aucun hackathon mis à jour !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Hackathon> getAll() {
        List<Hackathon> hackathons = new ArrayList<>();
        String req = "SELECT * FROM `hackathon`";

        try (PreparedStatement statement = connection.prepareStatement(req);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Hackathon hackathon = new Hackathon(
                        resultSet.getInt("id_hackathon"),
                        resultSet.getInt("id_organisateur"),
                        resultSet.getString("nom_hackathon"),
                        resultSet.getString("description"),
                        resultSet.getString("theme"),
                        resultSet.getTimestamp("date_debut").toLocalDateTime(),
                        resultSet.getTimestamp("date_fin").toLocalDateTime(),
                        resultSet.getString("lieu"),
                        resultSet.getInt("max_participants")
                );
                hackathons.add(hackathon);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hackathons;
    }

    @Override
    public void delete(Hackathon hackathon) {
        String query = "DELETE FROM `hackathon` WHERE `id_hackathon`= ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, hackathon.getId_hackathon());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Hackathon supprimé avec succès !");
            } else {
                System.out.println("Aucun hackathon trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public Hackathon getHackathonById(int id){
        Hackathon hackathon = new Hackathon();
        List<Hackathon> hackathons = getAll();
        for(Hackathon h :hackathons){
            if(h.getId_hackathon()==id){
                hackathon=h;
            }
        }
        return hackathon;
    }

    public List<Hackathon> getHackathonByIdOrganisateur(int idOrganisateur) {
        List<Hackathon> hackathons = new ArrayList<>();
        String req = "SELECT * FROM hackathon WHERE id_organisateur = ?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, idOrganisateur);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Hackathon h = new Hackathon();
                h.setId_hackathon(rs.getInt("id_hackathon"));
                h.setNom_hackathon(rs.getString("nom_hackathon"));
                h.setDescription(rs.getString("description"));
                h.setDate_debut(rs.getTimestamp("date_debut").toLocalDateTime());
                h.setDate_fin(rs.getTimestamp("date_fin").toLocalDateTime());
                h.setLieu(rs.getString("lieu"));
                h.setTheme(rs.getString("theme"));
                h.setMax_participants(rs.getInt("max_participants"));
                h.setId_organisateur(rs.getInt("id_organisateur"));

                hackathons.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hackathons;
    }

}
