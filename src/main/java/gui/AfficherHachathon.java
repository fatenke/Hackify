package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Hackathon;
import services.HackathonService;
import services.ParticipationService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AfficherHachathon {
    private final HackathonService hackathonService= new HackathonService();
    private final ParticipationService participationService= new ParticipationService();

    @FXML
    private GridPane gp_hackathon;
    @FXML
    void initialize() {
        loadHackathons();
    }
    public void loadHackathons(){
        List<Hackathon> hackathons = hackathonService.getAll();
        int columns = 2;
        int row = 0, col = 0;
        for (Hackathon h : hackathons) {
            VBox hackathonBox = new VBox();
            hackathonBox.getStyleClass().add("hackathon-card");

            Label nom = new Label(h.getNom_hackathon());
            nom.getStyleClass().add("hackathon-title");

            Label date = new Label("📅 " + h.getDate_debut().toString());
            date.getStyleClass().add("hackathon-date");

            Label lieu = new Label("📍 " + h.getLieu());
            lieu.getStyleClass().add("hackathon-lieu");
            HBox hbox=new HBox();
            Button updateButton = new Button("Update");
            Button deleteButton = new Button("Delete");
            hbox.getChildren().addAll(updateButton,deleteButton);
            deleteButton.getStyleClass().add("btn-participer");
            updateButton.getStyleClass().add("btn-participer");
            Button participateButton = new Button("Participate");
            participateButton.getStyleClass().add("btn-participer");


            updateButton.setOnAction(event -> ouvrirUpdateHackathon(h));
            participateButton.setOnAction(event -> participerHackathon(h));
            deleteButton.setOnAction(event -> supprimerHackathon(h));


            hackathonBox.getChildren().addAll(nom,date,lieu,hbox,participateButton);
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
            Stage stage = (Stage) gp_hackathon.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void participerHackathon(Hackathon hackathon) {
        try {
            /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/HackathonDetails.fxml"));*/
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterParticipation.fxml"));
            Parent newContent = loader.load();
            HackathonDetails controller = loader.getController();
            controller.setHackathon(hackathon);
            Stage stage = (Stage) gp_hackathon.getScene().getWindow();
            stage.getScene().setRoot(newContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*Participation p= new Participation(hackathon.getId_hackathon());
        participationService.add(p);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Participation");
        alert.setHeaderText(null);
        alert.setContentText("Vous avez participé à : " + hackathon.getNom_hackathon() + " Bonne chance!");
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to right, #1E90FF, #9370DB, #FF69B4); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-family: 'Arial';");
        alert.showAndWait();*/

    }
    public void supprimerHackathon(Hackathon hackathon){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment supprimer le hackathon : " + hackathon.getNom_hackathon() + " ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            hackathonService.delete(hackathon);
            System.out.println("Hackathon supprimé");
            gp_hackathon.getChildren().clear();
            loadHackathons();
        } else {
            System.out.println("Suppression annulée");
        }

    }

}

