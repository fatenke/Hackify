package services;

import Interfaces.GlobalInterface;
import models.Hackathon;
import models.Participation;
import models.User;
import util.MyConnection;
import util.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static services.MailService.*;


public class ParticipationService implements GlobalInterface<Participation> {
    private final Connection connection;
    public ParticipationService() {
        this.connection = MyConnection.getInstance().getConnection();
    }


    @Override
    public void add(Participation participation) {
        String req = "INSERT INTO `participation`(`id_hackathon`, `id_participant`, `date_inscription`, `statut`) VALUES (?,?,?,?)";
        PreparedStatement statement;

        try {
            // Récupérer l'utilisateur connecté
            User userConnecte = SessionManager.getSession(SessionManager.getLastSessionId());
            if (userConnecte == null) {
                System.out.println("Aucun utilisateur connecté !");
                return;
            }

            // Créer un nouvel objet Participation avec l'ID utilisateur
            Participation updatedParticipation = new Participation(
                    participation.getIdHackathon(),
                    userConnecte.getId()
            );

            statement = connection.prepareStatement(req);
            statement.setInt(1, updatedParticipation.getIdHackathon());
            statement.setInt(2, updatedParticipation.getIdParticipant());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(4, "En attente");


            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Participation ajoutée avec succès !");

            } else {
                System.out.println("Aucune ligne insérée");
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
    public List<Participation> getParticipationByHackathon(int id_hackathon){
        List<Participation> participations = new ArrayList<>();
        String query = "SELECT * FROM `participation` WHERE `id_hackathon` = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id_hackathon);
            ResultSet resultSet = preparedStatement.executeQuery();

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return participations;
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
            String req = "UPDATE participation SET statut = 'Refusé' WHERE id_hackathon = ? AND statut = 'En attente'";
            PreparedStatement statement = connection.prepareStatement(req);
            statement.setInt(1, idHackathon);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Toutes les participations en attente ont été refusées pour le hackathon ID: " + idHackathon);

            }

        } catch (SQLException ex) {
            System.out.println("Erreur lors de la mise à jour des participations : " + ex.getMessage());
        }
    }
    //integration
    public void getParticipantById(int id_participant){


    }

    //organisatuer
    public boolean validerParticipation(Participation participation) {
        System.out.println("Statut actuel : " + participation.getStatut());
        if (participation.getStatut().trim().equals("En attente")) {
            participation.setStatut("Validé");
            update(participation);
            return true;
        }
        return false;
    }
    public boolean refuserParticipation(Participation participation){
        System.out.println("Statut actuel : " + participation.getStatut());
        if (participation.getStatut().trim().equals("En attente")) {
            participation.setStatut("Refusé");
            update(participation);
            return true;
        }
        return false;


    }
    public void deleteByHackathonId(int idHackathon) {
        String req = "DELETE FROM participation WHERE id_hackathon = ?";
        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, idHackathon);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
