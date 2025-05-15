package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Evaluation;
import services.EvaluationService;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateEvaluation implements Initializable {

    private final EvaluationService ps = new EvaluationService();

    @FXML
    private TextField TFIdJury;
    @FXML
    private ComboBox<Integer> CBIdProjet; // Changed to ComboBox
    @FXML
    private ComboBox<Integer> CBIdHackathon; // Changed to ComboBox
    @FXML
    private TextField TFNoteTech;
    @FXML
    private TextField TFNoteInnov;
    @FXML
    private DatePicker TFDate;

    private Evaluation evaluation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadHackathonIds();
        CBIdHackathon.setOnAction(event -> updateProjectList());
    }

    /**
     * Loads all hackathon IDs into the ComboBox.
     */
    private void loadHackathonIds() {
        try {
            List<Integer> hackathonIds = ps.getHackathonIds();
            CBIdHackathon.getItems().addAll(hackathonIds);
        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible de charger les Hackathons.");
        }
    }

    /**
     * Updates the project list based on the selected hackathon ID.
     */
    private void updateProjectList() {
        CBIdProjet.getItems().clear(); // Clear existing projects
        Integer selectedHackathonId = CBIdHackathon.getValue();
        if (selectedHackathonId != null) {
            try {
                List<Integer> projectIds = ps.getProjectsByHackathonId(selectedHackathonId);
                CBIdProjet.getItems().addAll(projectIds);
            } catch (Exception e) {
                afficherAlerte("Erreur", "Impossible de charger les projets.");
            }
        }
    }

    /**
     * Pre-fills the fields with existing evaluation data.
     */
    public void setEvaluationData(Evaluation evaluation) {
        this.evaluation = evaluation;
        TFIdJury.setText(String.valueOf(evaluation.getIdJury()));
        CBIdHackathon.setValue(evaluation.getIdHackathon());
        updateProjectList(); // Ensure project list updates based on the hackathon
        CBIdProjet.setValue(evaluation.getIdProjet());
        TFNoteTech.setText(String.valueOf(evaluation.getNoteTech()));
        TFNoteInnov.setText(String.valueOf(evaluation.getNoteInnov()));
        TFDate.setValue(LocalDate.parse(evaluation.getDate()));
    }

    /**
     * Updates the evaluation after validating inputs.
     */
    @FXML
    void updateEvaluation(ActionEvent event) {
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

            evaluation.setIdJury(idJury);
            evaluation.setIdProjet(idProjet);
            evaluation.setIdHackathon(idHackathon);
            evaluation.setNoteTech(noteTech);
            evaluation.setNoteInnov(noteInnov);
            evaluation.setDate(date.toString());

            ps.update(evaluation);
            afficherAlerte("Succès", "L'évaluation a été mise à jour avec succès !");
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie", "Veuillez entrer des valeurs numériques valides.");
        }
    }

    /**
     * Returns to the evaluation display screen.
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
     * Displays an alert with a custom message.
     */
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
