package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Hackathon;
import models.Participation;
import models.User;
import services.HackathonService;
import services.ParticipationService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HackathonDetails {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
    ParticipationService participationService=new ParticipationService();
    HackathonService hackathonService=new HackathonService();
    @FXML
    private Label labelDescription;

    @FXML
    private Label labelDateDebut;

    @FXML
    private Label labelDateFin;

    @FXML
    private Label labelLieu;

    @FXML
    private Label labelNom;

    @FXML
    private Label labelTheme;
    private User user ;
    private Hackathon hackathon;
    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
        labelNom.setText(hackathon.getNom_hackathon());
        labelDescription.setText(hackathon.getDescription());
        labelDateDebut.setText(hackathon.getDate_debut().format(formatter));
        labelDateFin.setText(hackathon.getDate_fin().format(formatter));
        labelLieu.setText(hackathon.getLieu());
        labelTheme.setText(hackathon.getTheme());
    }

    @FXML
    void ParticiperIndiv(ActionEvent event) {
        System.out.println("Bouton Participer en Individuel cliqué !");
        Participation p= new Participation(hackathon.getId_hackathon() );
        participationService.add(p);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Participation");
        alert.setHeaderText(null);
        alert.setContentText("Vous avez participé à : " + hackathon.getNom_hackathon() + " Bonne chance!");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to right, #1E90FF, #9370DB, #FF69B4); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-family: 'Arial';");
        alert.showAndWait();

    }

    @FXML
    void ouvrirUpdateHackathon(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateHackathon.fxml"));
            Parent root = loader.load();
            UpdateHackathon hackathonToUpdate = loader.getController();
            hackathonToUpdate.setHackathon(hackathon);
            Stage stage = (Stage) labelDescription.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void supprimerHackathon(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer le hackathon : " + hackathon.getNom_hackathon() + " ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            participationService.deleteByHackathonId(hackathon.getId_hackathon());
            hackathonService.delete(hackathon);
            System.out.println("Hackathon supprimé");

            // Retour à la vue principale (ex: liste des hackathons)
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherHachathon.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) labelDescription.getScene().getWindow();
                stage.getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Suppression annulée");
        }
    }
    @FXML
    void afficherParticipants(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherParticipation.fxml"));
            Parent root = loader.load();
            AfficherParticipation voirParticipants = loader.getController();
            voirParticipants.setHackathon(hackathon);
            Stage stage = (Stage) labelDescription.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void ajouterprojet(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutProjet.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}

