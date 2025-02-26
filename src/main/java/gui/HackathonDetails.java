package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import models.Hackathon;
import models.Participation;
import services.ParticipationService;

public class HackathonDetails {
    ParticipationService participationService=new ParticipationService();
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

    private Hackathon hackathon;
    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
        labelNom.setText(hackathon.getNom_hackathon());
        labelDescription.setText(hackathon.getDescription());
        labelDateDebut.setText(hackathon.getDate_debut().toString());
        labelDateFin.setText(hackathon.getDate_fin().toString());
        labelLieu.setText(hackathon.getLieu());
    }

    @FXML
    void ParticiperIndiv(ActionEvent event) {
        System.out.println("Bouton Participer en Individuel cliqué !");
        Participation p= new Participation(hackathon.getId_hackathon());
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
    void participerEnEquipe(ActionEvent event) {

    }


}

