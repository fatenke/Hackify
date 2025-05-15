package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.io.IOException;

import static javafx.application.Application.launch;

public class TestFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainUI.fxml"));

        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Get screen size
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            // Set window size to 80% of screen
            double width = screenBounds.getWidth() * 0.8;
            double height = screenBounds.getHeight() * 0.8;

            primaryStage.setWidth(width);
            primaryStage.setHeight(height);

            // Optional: center the stage on the screen
            primaryStage.setX((screenBounds.getWidth() - width) / 2);
            primaryStage.setY((screenBounds.getHeight() - height) / 2);

            primaryStage.setTitle("Gestion hackathon");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
