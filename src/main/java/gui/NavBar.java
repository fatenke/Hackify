package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Objects;

public class NavBar {

    @FXML
    private Button btAfficherHackathon;

    @FXML
    private Button btAjouterHackathon;

    @FXML
    private Button btHistoriqueParticipation;

    @FXML
    private Button btAjouterProjet;

    @FXML
    private Button btAfficherProjet; // New button for Afficher Projet

    @FXML
    private Button btAjouterTechnologie; // New button for Ajouter Technologie

    @FXML
    void AfficherHackathon(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AfficherHachathon.fxml")));
            if (btAfficherHackathon != null && btAfficherHackathon.getScene() != null) {
                btAfficherHackathon.getScene().setRoot(root);
            } else {
                System.out.println("Scene or Button is null, cannot set root for AfficherHackathon.");
            }
        } catch (IOException e) {
            System.out.println("Error loading AfficherHackathon.fxml: " + e.getMessage());
        }
    }

    @FXML
    void AjouterHackathon(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AjouterHackathon.fxml")));
            if (btAjouterHackathon != null && btAjouterHackathon.getScene() != null) {
                btAjouterHackathon.getScene().setRoot(root);
            } else {
                System.out.println("Scene or Button is null, cannot set root for AjouterHackathon.");
            }
        } catch (IOException e) {
            System.out.println("Error loading AjouterHackathon.fxml: " + e.getMessage());
        }
    }

    @FXML
    void AfficherParticipation(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AfficherParticipation.fxml")));
            if (btHistoriqueParticipation != null && btHistoriqueParticipation.getScene() != null) {
                btHistoriqueParticipation.getScene().setRoot(root);
            } else {
                System.out.println("Scene or Button is null, cannot set root for AfficherParticipation.");
            }
        } catch (IOException e) {
            System.out.println("Error loading AfficherParticipation.fxml: " + e.getMessage());
        }
    }

    @FXML
    void AjouterProjet(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AjoutProjet.fxml")));
            if (btAjouterProjet != null && btAjouterProjet.getScene() != null) {
                btAjouterProjet.getScene().setRoot(root);
            } else {
                System.out.println("Scene or Button is null, cannot set root for AjouterProjet.");
            }
        } catch (IOException e) {
            System.out.println("Error loading AjouterProjet.fxml: " + e.getMessage());
        }
    }

    @FXML
    void AfficherProjet(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AfficherProjet.fxml")));
            if (btAfficherProjet != null && btAfficherProjet.getScene() != null) {
                btAfficherProjet.getScene().setRoot(root);
            } else {
                System.out.println("Scene or Button is null, cannot set root for AfficherProjet.");
            }
        } catch (IOException e) {
            System.out.println("Error loading AfficherProjet.fxml: " + e.getMessage());
        }
    }

    @FXML
    void AjouterTechnologie(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AjouterTechnologie.fxml")));
            if (btAjouterTechnologie != null && btAjouterTechnologie.getScene() != null) {
                btAjouterTechnologie.getScene().setRoot(root);
            } else {
                System.out.println("Scene or Button is null, cannot set root for AjouterTechnologie.");
            }
        } catch (IOException e) {
            System.out.println("Error loading AjouterTechnologie.fxml: " + e.getMessage());
        }
    }
}