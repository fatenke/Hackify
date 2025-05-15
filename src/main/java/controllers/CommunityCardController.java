package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Communaute;
import models.User;
import models.UserRole;
import services.CommunauteService;
import util.SessionManager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CommunityCardController {
    @FXML
    private GridPane gp_hackathon;
    @FXML
    private TextField searchField;
    private final CommunauteService commuService = new CommunauteService();
    private User currentUser;

    @FXML
    void initialize() {
        // Get current user from SessionManager
        currentUser = SessionManager.getSession(SessionManager.getLastSessionId());
        if (currentUser == null) {
            showAlert("Error", "No user logged in");
            return;
        }

        // Initialize search functionality
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                loadCommunities(newValue);
            });
        }

        loadCommunities("");
    }
    public void loadCommunities(String searchQuery) {
        try {
            List<Communaute> communautes;
            
            // Get communities based on user role
            String role = currentUser.getRole();
            System.out.println("Current user role: " + role); // Debug line
            
            if (role == null) {
                showAlert("Error", "User role is null");
                return;
            }
            
            // Compare with UserRole constants
            if (role.equals(UserRole.ORGANISATEUR)) {
                System.out.println("Role matched: ORGANISATEUR"); // Debug line
                communautes = commuService.getByOrganisateur(currentUser.getId());
            } else if (role.equals(UserRole.PARTICIPANT)) {
                communautes = commuService.getByParticipant(currentUser.getId());
            } else {
                showAlert("Error", "Invalid user role: " + role);
                return;
            }

            // Filter communities based on search query
            if (!searchQuery.isEmpty()) {
                communautes = communautes.stream()
                    .filter(c -> c.getNom().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            c.getDescription().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
            }

            if (communautes.isEmpty()) {
                VBox emptyBox = new VBox();
                emptyBox.setAlignment(Pos.CENTER);
                Label emptyLabel = new Label("Aucune communauté disponible");
                emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #82e9f1;");
                emptyBox.getChildren().add(emptyLabel);
                gp_hackathon.add(emptyBox, 0, 0);
                return;
            }
            
            gp_hackathon.getChildren().clear();
            int columns = 3;
            int row = 0;
            int col = 0;



            for (Communaute communaute : communautes) {
                VBox card = new VBox(15);
                card.setStyle("-fx-background-color: #286371; -fx-padding: 20px; -fx-background-radius: 15px;");
                card.setPrefSize(300, 250);
                card.setAlignment(Pos.TOP_LEFT);

                Label titleLabel = new Label(communaute.getNom());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #82e9f1;");

                Label descLabel = new Label(communaute.getDescription());
                descLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                descLabel.setWrapText(true);
                descLabel.setMaxHeight(100);

                Region spacer = new Region();
                VBox.setVgrow(spacer, Priority.ALWAYS);

                Button chatButton = new Button("Voir Chats");
                chatButton.setStyle("-fx-background-color: #82e9f1; -fx-text-fill: #286371; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 20;");
                chatButton.setOnAction(e -> navigateToChats(communaute));
                
                // Hover effect
                chatButton.setOnMouseEntered(e -> chatButton.setStyle("-fx-background-color: white; -fx-text-fill: #286371; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 20;"));
                chatButton.setOnMouseExited(e -> chatButton.setStyle("-fx-background-color: #82e9f1; -fx-text-fill: #286371; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 20;"));

                card.getChildren().addAll(titleLabel, descLabel, spacer, chatButton);
                
                // Card hover effect
                card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #286371; -fx-padding: 20px; -fx-background-radius: 15px; -fx-effect: dropshadow(three-pass-box, rgba(130,233,241,0.4), 10, 0, 0, 0);"));
                card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #286371; -fx-padding: 20px; -fx-background-radius: 15px;"));

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

