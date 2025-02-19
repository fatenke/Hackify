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
import models.Vote;
import services.VoteService;

import java.io.IOException;
import java.sql.SQLException;

public class AfficherVote {

    private final VoteService ps = new VoteService();

    @FXML
    private ListView<Vote> listView;

    @FXML
    void initialize() {
        ObservableList<Vote> votes = FXCollections.observableList(ps.getAll());


        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Vote vote, boolean empty) {
                super.updateItem(vote, empty);

                if (empty || vote == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create Delete button
                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> deleteVote(event, vote));

                    // Create Update button
                    Button updateButton = new Button("Update");
                    updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    updateButton.setOnAction(event -> openUpdatePage(event, vote));

                    // Create an HBox to hold both buttons
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.getChildren().addAll(deleteButton, updateButton);

                    setText(vote.toString());
                    setGraphic(hbox);
                }
            }
        });

        // Set ListView items
        listView.setItems(votes);
    }


    void deleteVote(ActionEvent event, Vote vote) {

        ps.delete(vote);
        listView.getItems().remove(vote); // Remove item from ListView
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText("Vote deleted successfully.");
        alert.showAndWait();

    }


    void openUpdatePage(ActionEvent event, Vote vote) {
        try {
            // Load the update page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateVote.fxml"));
            Parent root = loader.load();

            // Get the controller for the Update page
            UpdateVote controller = loader.getController();

            controller.setVoteData(vote);

            // Replace the current scene with the update page
            listView.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    @FXML
    void backToAjouterVote(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVote.fxml"));
            Parent root = loader.load();

            // Create a new scene and set it to the current stage
            Stage stage = (Stage) listView.getScene().getWindow();  // Get the current stage
            stage.setScene(new Scene(root));
            stage.show();  // Show the stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

