package tn.esprit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TestFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load and display the first window (AjouterEvaluation.fxml)
            FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/AjouterEvaluation.fxml"));
            Parent root1 = loader1.load();
            Scene scene1 = new Scene(root1);
            scene1.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            primaryStage.setScene(scene1);
            primaryStage.setTitle("Ajouter Evaluation");
            primaryStage.show();

            // Load and display the second window (AjouterVote.fxml)
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/AjouterVote.fxml"));
            Parent root2 = loader2.load();
            Scene scene2 = new Scene(root2);
            scene2.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            Stage secondStage = new Stage();
            secondStage.setScene(scene2);
            secondStage.setTitle("Ajouter Vote");
            secondStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
