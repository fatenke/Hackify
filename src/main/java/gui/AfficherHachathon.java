package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Hackathon;
import services.HackathonService;
import services.ParticipationService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;
import javafx.scene.text.Font;
import java.util.List;

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
        int columns = 3;
        int row = 0, col = 0;
        for (Hackathon h : hackathons) {
            StackPane stack = new StackPane();
            stack.getStyleClass().add("hackathon-card");

            stack.setPadding(new Insets(0));

            // Face avant du "cube"
            Rectangle frontFace = new Rectangle(330, 230);
            frontFace.setFill(Color.WHITE);
            frontFace.setStroke(Color.LIGHTGRAY);
            frontFace.setStrokeWidth(2);
            frontFace.setArcWidth(60);
            frontFace.setArcHeight(30);
            StackPane.setAlignment(frontFace, Pos.TOP_RIGHT);
            stack.getStyleClass().add("frontFace");


            // Conteneur du texte
            VBox textContainer = new VBox(5);
            textContainer.setAlignment(Pos.CENTER);
            textContainer.setPadding(new Insets(10));
            textContainer.getStyleClass().add("textContainer");

            Label nom = new Label(h.getNom_hackathon());
            nom.setFont(new Font("Arial", 16));
            nom.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

            Label date = new Label("📅 " + h.getDate_debut().toString());
            date.setStyle("-fx-text-fill: #555;");

            Label lieu = new Label("📍 " + h.getLieu());
            lieu.setStyle("-fx-text-fill: #777;");

            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER);
            Button updateButton = new Button("Update");
            Button deleteButton = new Button("Delete");
            Button participateButton = new Button("Participate");
            Button showButton = new Button("Voir Participants");

            updateButton.getStyleClass().add("btn-action");
            deleteButton.getStyleClass().add("btn-action");
            participateButton.getStyleClass().add("btn-action");
            showButton.getStyleClass().add("btn-action");

            updateButton.setOnAction(event -> ouvrirUpdateHackathon(h));
            deleteButton.setOnAction(event -> supprimerHackathon(h));
            participateButton.setOnAction(event -> participerHackathon(h));
            showButton.setOnAction(event -> afficherParticipants(h));


            hbox.getChildren().addAll(updateButton, deleteButton,showButton);
            textContainer.getChildren().addAll(nom, date, lieu, hbox, participateButton);

            // Superposer les éléments pour créer l'effet 3D
            stack.getChildren().addAll(frontFace, textContainer);
            StackPane.setMargin(frontFace, new Insets(-10, -12, 0, 0));

            // Ajout à la grille
            gp_hackathon.add(stack, col, row);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HackathonDetails.fxml"));
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
    public void afficherParticipants(Hackathon hackathon){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherParticipation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) gp_hackathon.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

