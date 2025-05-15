package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import models.Hackathon;
import models.Participation;
import models.User;
import services.ParticipationService;

import java.time.format.DateTimeFormatter;

public class HackathonDetails {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
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




}

