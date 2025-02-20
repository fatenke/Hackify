package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Evaluation;
import services.EvaluationService;
import tn.esprit.TestFX;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AjouterEvaluation {
    private final EvaluationService evaluationService = new EvaluationService();

    @FXML
    private Button btnGoToAjouterVote;

    @FXML
    private TextField TFIdJury;

    @FXML
    private ComboBox<Integer> CBIdProjet;

    @FXML
    private ComboBox<Integer> CBIdHackathon;

    @FXML
    private TextField TFNoteTech;

    @FXML
    private TextField TFNoteInnov;

    @FXML
    private DatePicker TFDate;

    @FXML
    public void initialize() {
        loadHackathons();
        CBIdHackathon.setOnAction(event -> loadProjectsForSelectedHackathon());
        TFDate.setValue(LocalDate.now());
    }

    private void loadHackathons() {
        try {
            List<Integer> hackathonIds = evaluationService.getHackathonIds();
            CBIdHackathon.getItems().addAll(hackathonIds);
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger les hackathons.");
        }
    }

    private void loadProjectsForSelectedHackathon() {
        CBIdProjet.getItems().clear();
        Integer selectedHackathon = CBIdHackathon.getValue();
        if (selectedHackathon != null) {
            try {
                List<Integer> projectIds = evaluationService.getProjectsByHackathonId(selectedHackathon);
                CBIdProjet.getItems().addAll(projectIds);
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de charger les projets pour ce hackathon.");
            }
        }
    }

    @FXML
    void ajouter(ActionEvent event) {
        if (TFIdJury.getText().isEmpty() || CBIdProjet.getValue() == null || CBIdHackathon.getValue() == null ||
                TFNoteTech.getText().isEmpty() || TFNoteInnov.getText().isEmpty() || TFDate.getValue() == null) {
            afficherAlerte("Erreur de saisie", "Tous les champs doivent être remplis !");
            return;
        }

        try {
            int idJury = Integer.parseInt(TFIdJury.getText());
            int idProjet = CBIdProjet.getValue();
            int idHackathon = CBIdHackathon.getValue();
            float noteTech = Float.parseFloat(TFNoteTech.getText());
            float noteInnov = Float.parseFloat(TFNoteInnov.getText());
            LocalDate date = TFDate.getValue();

            if (idJury <= 0 || idProjet <= 0 || idHackathon <= 0) {
                afficherAlerte("Erreur", "Les identifiants doivent être des nombres positifs.");
                return;
            }

            if (noteTech < 0 || noteTech > 20 || noteInnov < 0 || noteInnov > 20) {
                afficherAlerte("Erreur", "Les notes doivent être comprises entre 0 et 20.");
                return;
            }

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

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void viderChamps() {
        TFIdJury.clear();
        CBIdProjet.setValue(null);
        CBIdProjet.getItems().clear();
        CBIdHackathon.setValue(null);
        TFNoteTech.clear();
        TFNoteInnov.clear();
        TFDate.setValue(null);
    }

    public void goToAjouterVote(ActionEvent actionEvent) {
        try {
            // Load the AjouterVote.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterVote.fxml"));

            // Create a new stage for the AjouterVote scene
            Stage stage = new Stage();
            stage.setTitle("Ajouter Vote");
            stage.setScene(new Scene(root, 600, 539));
            stage.show();

            // Close the current window if necessary (optional)
            Stage currentStage = (Stage) btnGoToAjouterVote.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading AjouterVote.fxml.");
        }

    }
}
