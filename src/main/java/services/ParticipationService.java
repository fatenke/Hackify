package services;

import Interfaces.GlobalInterface;
import gui.HackathonDetails;
import models.Hackathon;
import models.Participation;
import util.MyConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static services.MailService.sendPlainTextEmail;

/*import static services.MailService.envoyerEmail;*/

public class ParticipationService implements GlobalInterface<Participation> {
    private final Connection connection;
    public ParticipationService() {
        this.connection = MyConnection.getInstance().getCnx();
    }

    //les emails
    public static void sendParticipationRequestEmail(Participation participation, String participantEmail) {
        HackathonService hs =new HackathonService();
        Hackathon hackathon= hs.getHackathonById(participation.getIdHackathon());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String subject = "📢 Confirmation de votre demande de participation au Hackathon";
        String content = "Bonjour,\n\n"
                + "Nous avons bien reçu votre demande de participation au hackathon " + hackathon.getNom_hackathon() + " qui aura lieu du " + hackathon.getDate_debut().format(formatter)+ " au " + hackathon.getDate_fin().format(formatter) + " à " + hackathon.getLieu() + ".\n"
                + "Votre demande est en cours de traitement et nous vous tiendrons informé(e) de l’état de votre inscription.\n\n"
                + "Cordialement,\nL'équipe d'organisation de " + hackathon.getNom_hackathon();
        sendPlainTextEmail(participantEmail, subject, content);
    }

    public static void sendParticipationAcceptanceEmail(Participation participation, String participantEmail) {
        HackathonService hs = new HackathonService();
        Hackathon hackathon = hs.getHackathonById(participation.getIdHackathon());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String subject = "🎉 Votre participation au Hackathon a été acceptée ! 🚀";
        String content = "Bonjour 👋,\n\n"
                + "Nous avons le plaisir de vous annoncer que votre demande de participation au hackathon "
                + hackathon.getNom_hackathon() + " a été **acceptée** ✅ ! Ce hackathon se déroulera du "
                + hackathon.getDate_debut().format(formatter) + " au " + hackathon.getDate_fin().format(formatter)
                + " à " + hackathon.getLieu() + ".\n\n"
                + "✨ Nous sommes impatients de vous accueillir et de découvrir vos idées brillantes ! 🚀\n\n"
                + "Si vous avez des questions ou besoin de plus d'informations, n'hésitez pas à nous contacter ! 📧\n\n"
                + "Cordialement,\nL'équipe d'organisation de " + hackathon.getNom_hackathon() + " 💡";
        sendPlainTextEmail(participantEmail, subject, content);
    }

    public static void sendParticipationRejectionEmail(Participation participation, String participantEmail) {
        HackathonService hs = new HackathonService();
        Hackathon hackathon = hs.getHackathonById(participation.getIdHackathon());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String subject = "🚫 Votre demande de participation au Hackathon a été rejetée";
        String content = "Bonjour 👋,\n\n"
                + "Nous sommes désolés de vous informer que votre demande de participation au hackathon "
                + hackathon.getNom_hackathon() + " n'a malheureusement pas été retenue ❌. Ce hackathon se déroulera du "
                + hackathon.getDate_debut().format(formatter) + " au " + hackathon.getDate_fin().format(formatter)
                + " à " + hackathon.getLieu() + ".\n\n"
                + "Nous vous remercions pour votre intérêt 🙏 et espérons que vous participerez à un futur hackathon ! 🌱\n\n"
                + "Cordialement,\nL'équipe d'organisation de " + hackathon.getNom_hackathon() + " 💡";
        sendPlainTextEmail(participantEmail, subject, content);
    }
    public static void sendHackathonFullEmail(Participation participation, String participantEmail) {
        HackathonService hs = new HackathonService();
        Hackathon hackathon = hs.getHackathonById(participation.getIdHackathon());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String subject = "⚠️ Le Hackathon est complet 🛑";
        String content = "Bonjour 👋,\n\n"
                + "Nous vous informons que malheureusement, le hackathon " + hackathon.getNom_hackathon()
                + " prévu du " + hackathon.getDate_debut().format(formatter) + " au "
                + hackathon.getDate_fin().format(formatter) + " à " + hackathon.getLieu()
                + " est désormais complet. 🚫\n\n"
                + "Nous avons été ravis de l'énorme intérêt porté à cet événement et nous espérons vous offrir d'autres opportunités très bientôt ! 💡\n\n"
                + "Restez connecté(e) pour plus d'événements à venir ✨.\n\n"
                + "Cordialement,\nL'équipe d'organisation de " + hackathon.getNom_hackathon() + " 💻";
        sendPlainTextEmail(participantEmail, subject, content);
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
                sendParticipationRequestEmail(participation,"fatenkerrou@gmail.com");
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

    //organisatuer
    public boolean validerParticipation(Participation participation) {
        if (participation.getStatut().equals("En attente")) {
            participation.setStatut("Validé");
            update(participation);
            sendParticipationAcceptanceEmail(participation,"");

            return true;
        }
        return false;
    }
}
