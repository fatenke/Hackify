package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Communaute;
import services.CommunauteService;

import java.io.IOException;
import java.util.List;

public class CommunityCardController {
    @FXML
    private GridPane gp_hackathon;
    private final CommunauteService commuService = new CommunauteService();

    @FXML
    void initialize() {
        loadCommunities();
    }
    public void loadCommunities() {
        try {
            List<Communaute> communautes = commuService.getAll();
            int columns = 3;
            int row = 0, col = 0;

            for (Communaute c : communautes) {
                // Create card container with fixed size
                VBox card = new VBox(10);
                card.setPrefSize(280, 200);
                card.setMaxSize(280, 200);
                card.setMinSize(280, 200);
                card.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
                    "-fx-background-radius: 8;" +
                    "-fx-padding: 15;"
                );

                // Name label
                Label nameLabel = new Label(c.getNom());
                nameLabel.setStyle(
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: #2C3E50;"
                );
                nameLabel.setWrapText(true);
                nameLabel.setMaxWidth(250);

                // Description label with max height
                Label descLabel = new Label(c.getDescription());
                descLabel.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: #7F8C8D;"
                );
                descLabel.setWrapText(true);
                descLabel.setMaxWidth(250);
                descLabel.setMaxHeight(80);

                // Button with hover effect
                Button chatButton = new Button("Voir Chats");
                chatButton.setStyle(
                    "-fx-background-color: #3498DB;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 5;" +
                    "-fx-padding: 8 16;"
                );
                chatButton.setOnMouseEntered(e -> 
                    chatButton.setStyle(
                        "-fx-background-color: #2980B9;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 8 16;"
                    )
                );
                chatButton.setOnMouseExited(e -> 
                    chatButton.setStyle(
                        "-fx-background-color: #3498DB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 8 16;"
                    )
                );

                // Add hover effect to card
                card.setOnMouseEntered(e -> 
                    card.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0, 0, 3);" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 15;"
                    )
                );
                card.setOnMouseExited(e -> 
                    card.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 15;"
                    )
                );

                // Set up button action
                final Communaute community = c;
                chatButton.setOnAction(event -> navigateToChats(community));

                // Add spacing at bottom to push button down
                Region spacer = new Region();
                VBox.setVgrow(spacer, Priority.ALWAYS);

                // Add all elements to card
                card.getChildren().addAll(nameLabel, descLabel, spacer, chatButton);

                // Add card to grid
                gp_hackathon.add(card, col, row);
                col++;
                if (col == columns) {
                    col = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load communities: " + e.getMessage());
        }
    }

    private void navigateToChats(Communaute communaute) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AfficherChats.fxml"));
            Parent newContent = loader.load();

            // Pass the selected community to the new controller
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

