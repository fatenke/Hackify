package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import java.io.IOException;

public class TestFX extends Application {
    private TabPane tabPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create TabPane
            tabPane = new TabPane();

            // Load initial content for tabs
            loadTabs();

            // Create Scene
            Scene scene = new Scene(tabPane, 650, 600);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            primaryStage.setTitle("Evaluation Management");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading FXML files.");
        }
    }

    private void loadTabs() {
        try {
            // Load only the content inside each tab
            Parent ajouterEvaluation = FXMLLoader.load(getClass().getResource("/AjouterEvaluation.fxml"));
            Parent afficherEvaluation = FXMLLoader.load(getClass().getResource("/AfficherEvaluation.fxml"));
            Parent ajouterVote = FXMLLoader.load(getClass().getResource("/AjouterVote.fxml"));
            Parent afficherVote = FXMLLoader.load(getClass().getResource("/AfficherVote.fxml"));
            Parent complexEvaluationAnalysis = FXMLLoader.load(getClass().getResource("/ComplexEvaluationAnalysis.fxml"));

            // Create Tabs
            Tab tab1 = new Tab("Ajouter Evaluation", ajouterEvaluation);
            Tab tab2 = new Tab("Afficher Evaluation", afficherEvaluation);
            Tab tab3 = new Tab("Ajouter Vote", ajouterVote);
            Tab tab4 = new Tab("Afficher Vote", afficherVote);
            Tab tab5 = new Tab("Complex Evaluation Analysis", complexEvaluationAnalysis);

            // Make tabs permanent
            tab1.setClosable(false);
            tab2.setClosable(false);
            tab3.setClosable(false);
            tab4.setClosable(false);
            tab5.setClosable(false);

            // Add all tabs
            tabPane.getTabs().setAll(tab1, tab2, tab3, tab4, tab5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
