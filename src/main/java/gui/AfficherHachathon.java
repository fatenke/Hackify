package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Hackathon;
import services.HackathonService;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.List;

public class AfficherHachathon {
    private final HackathonService hackathonService= new HackathonService();

    @FXML
    private GridPane gp_hackathon;
    @FXML
    void initialize() {
        List<Hackathon> hackathons = hackathonService.getAll();
        int columns = 2;
        int row = 0, col = 0;

        for (Hackathon h : hackathons) {

            VBox hackathonBox = new VBox();
            hackathonBox.setStyle("-fx-border-color: black; -fx-padding: 10px; -fx-spacing: 5px; -fx-alignment: center;");

            Label hackathonLabel = new Label(h.toString());
            Button updateButton = new Button("Update");

            updateButton.setOnAction(event -> ouvrirUpdateHackathon(h));

            hackathonBox.getChildren().addAll(hackathonLabel, updateButton);
            gp_hackathon.add(hackathonBox, col, row);

            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }

    }
    private void ouvrirUpdateHackathon (Hackathon hackathon){
        System.out.println("ID du hackathon à mettre à jour: " + hackathon.getId_hackathon());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateHackathon.fxml"));
            Parent root = loader.load();

            UpdateHackathon hackathonToUpdate = loader.getController();
            hackathonToUpdate.setHackathon(hackathon);

            Stage stage = new Stage();
            stage.setTitle("Update Hackathon");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

