package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.Hackathon;

public class HackathonDetails {

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


}

