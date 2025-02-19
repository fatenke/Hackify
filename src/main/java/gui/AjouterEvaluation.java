package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import models.Evaluation;
import services.EvaluationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterEvaluation {
    private final EvaluationService evaluationService = new EvaluationService();

    @FXML
    private TextField TFIdJury;

    @FXML
    private TextField TFIdProjet;

    @FXML
    private TextField TFIdHackathon;

    @FXML
    private TextField TFNoteTech;

    @FXML
    private TextField TFNoteInnov;

    @FXML
    private DatePicker TFDate;

    @FXML
    void ajouter(ActionEvent event) {
        // Vérifier si les champs sont vides
        if (TFIdJury.getText().isEmpty() || TFIdProjet.getText().isEmpty() || TFIdHackathon.getText().isEmpty() ||
                TFNoteTech.getText().isEmpty() || TFNoteInnov.getText().isEmpty() || TFDate.getValue() == null) {
            afficherAlerte("Erreur de saisie", "Tous les champs doivent être remplis !");
            return;
        }

        try {
            int idJury = Integer.parseInt(TFIdJury.getText());
            int idProjet = Integer.parseInt(TFIdProjet.getText());
            int idHackathon = Integer.parseInt(TFIdHackathon.getText());
            float noteTech = Float.parseFloat(TFNoteTech.getText());
            float noteInnov = Float.parseFloat(TFNoteInnov.getText());
            LocalDate date = TFDate.getValue();

            // Vérifier les valeurs des nombres
            if (idJury <= 0 || idProjet <= 0 || idHackathon <= 0) {
                afficherAlerte("Erreur", "Les identifiants doivent être des nombres positifs.");
                return;
            }

            if (noteTech < 0 || noteTech > 20 || noteInnov < 0 || noteInnov > 20) {
                afficherAlerte("Erreur", "Les notes doivent être comprises entre 0 et 20.");
                return;
            }

            // Création de l'évaluation et ajout à la base de données
            Evaluation p = new Evaluation(noteTech, noteInnov, date.toString(), idJury, idProjet, idHackathon);
            evaluationService.add(p);

            afficherAlerte("Succès", "L'évaluation a été ajoutée avec succès !");
            viderChamps();

        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie", "Veuillez entrer des valeurs numériques valides.");
        }
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherEvaluation.fxml"));
            TFIdJury.getScene().setRoot(root);
        } catch (IOException e) {
            afficherAlerte("Erreur", "Impossible d'afficher la fenêtre.");
        }
    }

    /**
     * Affiche une alerte avec un message personnalisé.
     */
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Vide tous les champs après l'ajout.
     */
    private void viderChamps() {
        TFIdJury.clear();
        TFIdProjet.clear();
        TFIdHackathon.clear();
        TFNoteTech.clear();
        TFNoteInnov.clear();
        TFDate.setValue(null);
    }
}
