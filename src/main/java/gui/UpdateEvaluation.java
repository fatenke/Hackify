package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Evaluation;
import services.EvaluationService;

import java.time.LocalDate;

public class UpdateEvaluation {

    private final EvaluationService ps = new EvaluationService();

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

    private Evaluation evaluation;

    /**
     * Pré-remplit les champs avec les données actuelles de l'évaluation.
     */
    public void setEvaluationData(Evaluation evaluation) {
        this.evaluation = evaluation;
        TFIdJury.setText(String.valueOf(evaluation.getIdJury()));
        TFIdProjet.setText(String.valueOf(evaluation.getIdProjet()));
        TFIdHackathon.setText(String.valueOf(evaluation.getIdHackathon()));
        TFNoteTech.setText(String.valueOf(evaluation.getNoteTech()));
        TFNoteInnov.setText(String.valueOf(evaluation.getNoteInnov()));
        TFDate.setValue(LocalDate.parse(evaluation.getDate()));
    }

    /**
     * Met à jour l'évaluation après vérification des entrées.
     */
    @FXML
    void updateEvaluation(ActionEvent event) {
        // Vérification des champs vides
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

            // Vérification des valeurs positives
            if (idJury <= 0 || idProjet <= 0 || idHackathon <= 0) {
                afficherAlerte("Erreur", "Les identifiants doivent être des nombres positifs.");
                return;
            }

            // Vérification des notes (0 - 20)
            if (noteTech < 0 || noteTech > 20 || noteInnov < 0 || noteInnov > 20) {
                afficherAlerte("Erreur", "Les notes doivent être comprises entre 0 et 20.");
                return;
            }

            // Mise à jour des données de l'évaluation
            evaluation.setIdJury(idJury);
            evaluation.setIdProjet(idProjet);
            evaluation.setIdHackathon(idHackathon);
            evaluation.setNoteTech(noteTech);
            evaluation.setNoteInnov(noteInnov);
            evaluation.setDate(date.toString());

            // Mise à jour en base de données
            ps.update(evaluation);

            afficherAlerte("Succès", "L'évaluation a été mise à jour avec succès !");
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie", "Veuillez entrer des valeurs numériques valides.");
        }
    }

    /**
     * Retour à l'affichage des évaluations.
     */
    @FXML
    void backToAfficherEvaluation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvaluation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) TFIdJury.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
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
}
