package controllers;

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
import models.Communaute;
import models.Hackathon;
import services.CommunauteService;
import services.ParticipationService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CommunityCardController {
    private final CommunauteService commuService= new CommunauteService();
    private final ParticipationService participationService= new ParticipationService();

    @FXML
    private GridPane gp_hackathon;
    @FXML
    void initialize() {
        loadCommunities();
    }
    public void loadCommunities(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        List<Communaute> communautes = commuService.getAll();
        int columns = 3;
        int row = 0, col = 0;
        for (Communaute c : communautes) {
            StackPane stack = new StackPane();
            stack.getStyleClass().add("hackathon-card");
            stack.setPadding(new Insets(0));
            Rectangle frontFace = new Rectangle(330, 230);
            frontFace.setFill(Color.WHITE);
            frontFace.setStroke(Color.LIGHTGRAY);
            frontFace.setStrokeWidth(2);
            frontFace.setArcWidth(60);
            frontFace.setArcHeight(30);
            StackPane.setAlignment(frontFace, Pos.TOP_RIGHT);
            stack.getStyleClass().add("frontFace");


            VBox textContainer = new VBox(5);
            textContainer.setAlignment(Pos.CENTER);
            textContainer.setPadding(new Insets(10));
            textContainer.getStyleClass().add("textContainer");

            Label nom = new Label(c.getNom());
            nom.setFont(new Font("Arial", 16));
            nom.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");



            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER);


            Button chatButton = new Button("Voir Chats");
            chatButton.getStyleClass().add("btn-action");
            chatButton.setOnAction(event -> afficherChats(c));
            hbox.getChildren().addAll(chatButton);


            stack.getChildren().addAll(frontFace, textContainer);
            StackPane.setMargin(frontFace, new Insets(-10, -12, 0, 0));

            gp_hackathon.add(stack, col, row);
            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }

    }

    private void afficherChats(Communaute communaute) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AfficherChats.fxml"));
            Parent newContent = loader.load();

            // Pass the selected community to the new controller if needed
            AfficherChatsController controller = loader.getController();
            controller.setCommunaute(communaute.getId());

            Stage stage = (Stage) gp_hackathon.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the chat view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

