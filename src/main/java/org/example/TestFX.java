package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TestFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main layout FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainLayout.fxml"));
            Parent root = loader.load();

            // Create the scene
            Scene scene = new Scene(root , 1200, 800);

            // Set the title of the stage
            primaryStage.setTitle("Hackify - Communautes");

            // Set the scene to the stage
            primaryStage.setScene(scene);

            // Show the stage
            primaryStage.show();
        } catch (Exception e) {
            // Print the error message if the FXML file cannot be loaded
            System.err.println("Error loading FXML file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


}
