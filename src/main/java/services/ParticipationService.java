package services;

import Interfaces.GlobalInterface;
import models.Participation;
import util.MyConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParticipationService implements GlobalInterface<Participation> {
    private final Connection connection;
    public ParticipationService() {
        this.connection = MyConnection.getInstance().getCnx();
    }


    @Override
    public void add(Participation participation) {
        String req = "INSERT INTO `participation`(`id_hackathon`, `id_participant`) VALUES (?,?)";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(req);
            statement.setInt(1, participation.getIdHackathon());
            statement.setInt(2, participation.getIdParticipant());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Participation ajouté avec succès !");
            } else {
                System.out.println(" Aucune ligne insérée");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Participation participation) {
        String req = "UPDATE `participation` SET `statut`=? WHERE `id_participation`= ? ";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setString(1, participation.getStatut());
            statement.setInt(2, participation.getIdParticipation());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Participation mis à jour avec succès !");
            } else {
                System.out.println("Aucune participation mis à jour");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    @Override
    public List<Participation> getAll() {
        List<Participation> participations = new ArrayList<>();
        String req = "SELECT * FROM `participation`";
        try (PreparedStatement statement = connection.prepareStatement(req);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Participation participation = new Participation(
                        resultSet.getInt("id_participation"),
                        resultSet.getInt("id_hackathon"),
                        resultSet.getInt("id_participant"),
                        resultSet.getTimestamp("date_inscription").toLocalDateTime(),
                        resultSet.getString("statut")
                );
                participations.add(participation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participations;

    }

    @Override
    public void delete(Participation participation) {
        String req = "DELETE FROM `participation` WHERE `id_participation`= ?";

        try (PreparedStatement statement = connection.prepareStatement(req)) {
            statement.setInt(1, participation.getIdParticipation());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Participation supprimé avec succès !");
            } else {
                System.out.println("Aucun participation trouvé !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    public Participation getParticipationByHackathon(int id_hackathon){
        Participation participation = new Participation();
        List<Participation> participations = getAll();
        for(Participation p :participations){
            if(p.getIdHackathon()==id_hackathon){
                participation=p;
            }
        }
        return participation;
    }

    public int getNebrParticipantPerHackathon(int id_hackathon){
        int count = 0;
        try {
            String req = "SELECT COUNT(*) FROM participation WHERE id_hackathon = ? AND statut = 'validé'";
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, id_hackathon);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération du nombre de participants : " + ex.getMessage());
        }
        return count;
    }
    public void refuserParticipationsEnAttente(int idHackathon) {
        try {
            String req = "UPDATE participation SET statut = 'refusé' WHERE id_hackathon = ? AND statut = 'en attente'";
            PreparedStatement ps = connection.prepareStatement(req);
            ps.setInt(1, idHackathon);
            ps.executeUpdate();
            System.out.println("Toutes les participations en attente ont été refusées pour le hackathon ID: " + idHackathon);
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la mise à jour des participations : " + ex.getMessage());
        }
    }
}
