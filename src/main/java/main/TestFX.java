package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class TestFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {
        /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterHackathon.fxml"));*/
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherHachathon.fxml"));
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            /*primaryStage.setFullScreen(true);*/
            primaryStage.setMaximized(true);
            primaryStage.setTitle("Gestion hackathon");
            primaryStage.setScene(scene);
            primaryStage.show();



        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
