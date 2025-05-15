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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class ComplexEvaluationAnalysis {

    private final EvaluationService evaluationService = new EvaluationService();

    @FXML
    private ListView<String> analysisListView;

    @FXML
    private ListView<String> leaderboardListView;

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

        analysisListView.getItems().add("Analysing evaluations...");

        for (Evaluation eval : evaluations) {
            analysisListView.getItems().add("Projet ID: " + eval.getIdProjet() + " | Tech: " + eval.getNoteTech() + " | Innov: " + eval.getNoteInnov());

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
        highestTechProjectLabel.setText("Projet avec la plus haute note technique: " + highestTechId + " (Score: " + maxTech + ")");
        highestInnovProjectLabel.setText("Projet avec la plus haute note d'innovation: " + highestInnovId + " (Score: " + maxInnov + ")");

        analysisListView.getItems().add("\nSummary:");
        analysisListView.getItems().add("Nombre des évaluations: " + evaluations.size());
        analysisListView.getItems().add("Interval des notes techniques: " + evaluations.stream().mapToDouble(Evaluation::getNoteTech).min().orElse(0) + " - " + maxTech);
        analysisListView.getItems().add("Interval des notes d'innovation: " + evaluations.stream().mapToDouble(Evaluation::getNoteInnov).min().orElse(0) + " - " + maxInnov);

        // Leaderboard logic
        List<Evaluation> top3Projects = evaluations.stream()
                .sorted(Comparator.comparingDouble((Evaluation e) -> e.getNoteTech() + e.getNoteInnov()).reversed())
                .limit(3)
                .collect(Collectors.toList());

        leaderboardListView.getItems().add("Top 3 projets avec les meilleures notes combinées:");
        for (int i = 0; i < top3Projects.size(); i++) {
            Evaluation project = top3Projects.get(i);
            leaderboardListView.getItems().add((i + 1) + ". Projet ID: " + project.getIdProjet() +
                    " - Score Technique: " + project.getNoteTech() +
                    " | Score Innovation: " + project.getNoteInnov() +
                    " | Score Total: " + (project.getNoteTech() + project.getNoteInnov()));
        }
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
