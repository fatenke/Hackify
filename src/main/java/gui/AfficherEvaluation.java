package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import models.Evaluation;
import services.EvaluationService;

import java.io.IOException;
import java.sql.SQLException;

public class AfficherEvaluation {

    private final EvaluationService ps = new EvaluationService();

    @FXML
    private ListView<Evaluation> listView;

    @FXML
    void initialize() {
        ObservableList<Evaluation> evaluations = FXCollections.observableList(ps.getAll());

        // Set the cell factory to display evaluations with Delete and Update buttons
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Evaluation evaluation, boolean empty) {
                super.updateItem(evaluation, empty);

                if (empty || evaluation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create Delete button
                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> deleteEvaluation(event, evaluation));

                    // Create Update button
                    Button updateButton = new Button("Update");
                    updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    updateButton.setOnAction(event -> openUpdatePage(event, evaluation));

                    // Create an HBox to hold both buttons
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.getChildren().addAll(deleteButton, updateButton);

                    setText(evaluation.toString());  // Display evaluation details
                    setGraphic(hbox);
                }
            }
        });

        // Set ListView items
        listView.setItems(evaluations);
    }

    // Delete Evaluation method
    void deleteEvaluation(ActionEvent event, Evaluation evaluation) {

            ps.delete(evaluation);
            listView.getItems().remove(evaluation); // Remove item from ListView
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Evaluation deleted successfully.");
            alert.showAndWait();

    }

    // Open Update Evaluation Page
    void openUpdatePage(ActionEvent event, Evaluation evaluation) {
        try {
            // Load the update page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateEvaluation.fxml"));
            Parent root = loader.load();

            // Get the controller for the Update page
            UpdateEvaluation controller = loader.getController();
            // Pass the current Evaluation object to the controller
            controller.setEvaluationData(evaluation);

            // Replace the current scene with the update page
            listView.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    @FXML
    void backToAjouterEvaluation(ActionEvent event) {
        try {
            // Load the AjouterEvaluation.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvaluation.fxml"));
            Parent root = loader.load();

            // Create a new scene and set it to the current stage
            Stage stage = (Stage) listView.getScene().getWindow();  // Get the current stage
            stage.setScene(new Scene(root));  // Set the new scene (AjouterEvaluation.fxml)
            stage.show();  // Show the stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

