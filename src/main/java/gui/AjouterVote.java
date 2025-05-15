package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Vote;
import services.VoteService;
import services.EvaluationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AjouterVote {
    private final VoteService voteService = new VoteService();
    private final EvaluationService evaluationService = new EvaluationService();

    @FXML
    private TextField TFIdVotant;

    @FXML
    private ComboBox<Integer> CBIdHackathon; // Changed from TextField to ComboBox

    @FXML
    private ComboBox<Integer> CBIdProjet; // Changed from TextField to ComboBox

    @FXML
    private ComboBox<Integer> CBIdEvaluation;

    @FXML
    private TextField TFValeurVote;

    @FXML
    private DatePicker TFDate;
    @FXML
    private Button btnGoToAjouterEvaluation;

    @FXML
    public void initialize() {
        loadHackathons();
        loadEvaluations();
        CBIdHackathon.setOnAction(event -> loadProjectsForHackathon());
        TFDate.setValue(LocalDate.now());
    }

    private void loadEvaluations() {
        try {
            List<Integer> evaluationIds = evaluationService.getEvaluationIds();
            CBIdEvaluation.setItems(FXCollections.observableArrayList(evaluationIds));
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger les évaluations.");
        }
    }
    private void loadHackathons() {
        try {
            List<Integer> hackathonIds = evaluationService.getHackathonIds();
            CBIdHackathon.setItems(FXCollections.observableArrayList(hackathonIds));
        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger les hackathons.");
        }
    }

    private void loadProjectsForHackathon() {
        Integer selectedHackathon = CBIdHackathon.getValue();
        if (selectedHackathon != null) {
            try {
                List<Integer> projectIds = evaluationService.getProjectsByHackathonId(selectedHackathon);
                CBIdProjet.setItems(FXCollections.observableArrayList(projectIds));
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de charger les projets pour ce hackathon.");
            }
        }
    }
    @FXML
    void ajouter(ActionEvent event) {
        if (TFIdVotant.getText().isEmpty() || CBIdProjet.getValue() == null || CBIdHackathon.getValue() == null ||
                CBIdEvaluation.getValue() == null || TFValeurVote.getText().isEmpty() || TFDate.getValue() == null) {
            afficherAlerte("Erreur de saisie", "Tous les champs doivent être remplis !");
            return;
        }

        try {
            int idVotant = Integer.parseInt(TFIdVotant.getText());
            int idProjet = CBIdProjet.getValue();
            int idHackathon = CBIdHackathon.getValue();
            int idEvaluation = CBIdEvaluation.getValue(); // Get value from ComboBox
            float valeurVote = Float.parseFloat(TFValeurVote.getText());
            LocalDate date = TFDate.getValue();

            if (idVotant <= 0 || idProjet <= 0 || idHackathon <= 0 || idEvaluation <= 0) {
                afficherAlerte("Erreur", "Les identifiants doivent être des nombres positifs.");
                return;
            }

            if (valeurVote < 0 || valeurVote > 10) {
                afficherAlerte("Erreur", "La valeur du vote doit être comprise entre 0 et 10.");
                return;
            }

            Vote p = new Vote(valeurVote, date.toString(), idEvaluation, idVotant, idProjet, idHackathon);
            voteService.add(p);

            afficherAlerte("Succès", "Le vote a été ajouté avec succès !");
            viderChamps();

        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie", "Veuillez entrer des valeurs numériques valides.");
        }
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherVote.fxml"));
            TFIdVotant.getScene().setRoot(root);
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
        TFIdVotant.clear();
        CBIdProjet.getSelectionModel().clearSelection();
        CBIdHackathon.getSelectionModel().clearSelection();
        CBIdEvaluation.getSelectionModel().clearSelection();
        TFValeurVote.clear();
        TFDate.setValue(null);
    }

    public void goToAjouterEvaluation(ActionEvent actionEvent) {
        try {
            // Load the AjouterEvaluation.fxml
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterEvaluation.fxml"));

            // Create a new stage for the AjouterEvaluation scene
            Stage stage = new Stage();
            stage.setTitle("Ajouter Evaluation");
            stage.setScene(new Scene(root, 600, 539));
            stage.show();

            // Optionally close the current window (if desired)
            Stage currentStage = (Stage) btnGoToAjouterEvaluation.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading AjouterEvaluation.fxml.");
        }
    }

}
