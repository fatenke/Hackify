package services;

import Interfaces.GlobalInterface;
import models.Hackathon;
import util.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HackathonService implements GlobalInterface<Hackathon> {

    private final Connection connection;

    public HackathonService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    @Override
    public void add(Hackathon hackathon) {
        String req = "INSERT INTO `hackathon`(`id_organisateur`, `nom_hackathon`, `description`, `date_debut`, `date_fin`, `lieu`, `theme`, `max_participants`, `type_participation`)  VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(req);
            statement.setInt(1, hackathon.getId_organisateur());
            statement.setString(2, hackathon.getNom_hackathon());
            statement.setString(3, hackathon.getDescription());
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(hackathon.getDate_debut()));
            statement.setTimestamp(5, java.sql.Timestamp.valueOf(hackathon.getDate_fin()));
            statement.setString(6, hackathon.getLieu());
            statement.setString(7, hackathon.getTheme());
            statement.setInt(8, hackathon.getMax_participants());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Hackathon ajouté avec succès !");
            } else {
                System.out.println(" Aucune ligne insérée, vérifie la requête !");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Hackathon hackathon) {

        String req = "UPDATE `hackathon` SET `nom_hackathon`=?,`description`=?,`date_debut`=?,`date_fin`=?,`lieu`=?,`theme`=?,`max_participants`='?',`type_participation`='?' WHERE `id_hackathon`= ? ";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, hackathon.getNom_hackathon());
            statement.setString(2, hackathon.getDescription());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(hackathon.getDate_debut()));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(hackathon.getDate_fin()));
            statement.setString(5, hackathon.getLieu());
            statement.setString(6, hackathon.getTheme());
            statement.setInt(7, hackathon.getMax_participants());
            statement.setInt(9, hackathon.getId_hackathon());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Hackathon mis à jour avec succès !");
            } else {
                System.out.println("Aucun hackathon mis à jour!");
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
}
