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
            // Load only the content inside each tab, only once
            Parent ajouterEvaluation = FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource("AjouterEvaluation.fxml"));
            Parent afficherEvaluation = FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource("AfficherEvaluation.fxml"));
            Parent ajouterVote = FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource("AjouterVote.fxml"));
            Parent afficherVote = FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource("AfficherVote.fxml"));

            // Create Tabs (They remain permanent)
            Tab tab1 = new Tab("Ajouter Evaluation", ajouterEvaluation);
            Tab tab2 = new Tab("Afficher Evaluation", afficherEvaluation);
            Tab tab3 = new Tab("Ajouter Vote", ajouterVote);
            Tab tab4 = new Tab("Afficher Vote", afficherVote);

            // Make tabs permanent (don't let them close)
            tab1.setClosable(false);
            tab2.setClosable(false);
            tab3.setClosable(false);
            tab4.setClosable(false);

            // Add tabs only once
            tabPane.getTabs().setAll(tab1, tab2, tab3, tab4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Call this method when updating data, instead of reloading everything
    public void updateContent() {
        try {
            // Update only the content in the existing tabs, not the whole tab set
            Parent newAjouterEvaluation = FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource("AjouterEvaluation.fxml"));
            Parent newAfficherEvaluation = FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource("AfficherEvaluation.fxml"));
            Parent newAjouterVote = FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource("AjouterVote.fxml"));
            Parent newAfficherVote = FXMLLoader.load(Thread.currentThread().getContextClassLoader().getResource("AfficherVote.fxml"));

            tabPane.getTabs().get(0).setContent(newAjouterEvaluation);
            tabPane.getTabs().get(1).setContent(newAfficherEvaluation);
            tabPane.getTabs().get(2).setContent(newAjouterVote);
            tabPane.getTabs().get(3).setContent(newAfficherVote);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
