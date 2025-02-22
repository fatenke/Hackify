package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import models.Evaluation;
import services.EvaluationService;

import java.io.IOException;
import java.util.List;

public class ComplexEvaluationAnalysis {

    private final EvaluationService evaluationService = new EvaluationService();

    @FXML
    private ListView<String> analysisListView;

    @FXML
    private Label averageTechLabel;

    @FXML
    private Label averageInnovLabel;

    @FXML
    private Label highestTechProjectLabel;

    @FXML
    private Label highestInnovProjectLabel;

    @FXML
    void initialize() {
        List<Evaluation> evaluations = evaluationService.getAll();

        if (evaluations.isEmpty()) {
            analysisListView.getItems().add("No evaluations found.");
            return;
        }

        double totalTech = 0;
        double totalInnov = 0;
        int highestTechId = -1;
        int highestInnovId = -1;
        float maxTech = Float.MIN_VALUE;
        float maxInnov = Float.MIN_VALUE;

        for (Evaluation eval : evaluations) {
            totalTech += eval.getNoteTech();
            totalInnov += eval.getNoteInnov();

            if (eval.getNoteTech() > maxTech) {
                maxTech = eval.getNoteTech();
                highestTechId = eval.getIdProjet();
            }

            if (eval.getNoteInnov() > maxInnov) {
                maxInnov = eval.getNoteInnov();
                highestInnovId = eval.getIdProjet();
            }
        }

        double averageTech = totalTech / evaluations.size();
        double averageInnov = totalInnov / evaluations.size();

        averageTechLabel.setText(String.format("Moyenne des notes techniques: %.2f", averageTech));
        averageInnovLabel.setText(String.format("Moyenne des notes d'innovations: %.2f", averageInnov));
        highestTechProjectLabel.setText("Identifiant du Projet avec la plus haute note technique: " + highestTechId + " (Score: " + maxTech + ")");
        highestInnovProjectLabel.setText("Identifiant du Projet avec la plus haute note d'innovation " + highestInnovId + " (Score: " + maxInnov + ")");

        analysisListView.getItems().add("Nombre des évaluations: " + evaluations.size());
        analysisListView.getItems().add("Interval des notes techniques: " + evaluations.stream().mapToDouble(Evaluation::getNoteTech).min().orElse(0) + " - " + maxTech);
        analysisListView.getItems().add("Interval des notes d'innovation: " + evaluations.stream().mapToDouble(Evaluation::getNoteInnov).min().orElse(0) + " - " + maxInnov);
    }

    public void backToAfficherEvaluation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvaluation.fxml"));
            Parent afficherEvaluationView = loader.load();
            Scene scene = new Scene(afficherEvaluationView);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Afficher Evaluation");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading AfficherEvaluation.fxml");
        }
    }
}
