package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.Communaute;
import services.CommunauteService;

import java.io.IOException;
import java.util.List;

public class MainLayoutController {
    @FXML
    private FlowPane communityCardsContainer;
    @FXML
    private Button loadCommunitiesButton;

    private CommunauteService communauteService = new CommunauteService();

    @FXML
    public void initialize() {
        handleLoadCommunities();
    }

    @FXML
    private void handleLoadCommunities() {
        communityCardsContainer.getChildren().clear();
        List<Communaute> communities = communauteService.getAll();
        for (Communaute communaute : communities) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CommunityCard.fxml")); // Adjust path if needed
                VBox card = loader.load();
                CommunityCardController cardController = loader.getController();
                cardController.setCommunityDetails(
                        communaute.getId(),
                        communaute.getNom(),
                        communaute.getDescription(),
                        communaute.getDateCreation()
                );
                communityCardsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddCommunity() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreateCommunity.fxml")); // Adjust path if needed
            Parent root = loader.load();
            CreateCommunityController createController = loader.getController();
            createController.setMainLayoutController(this); // Pass reference to refresh after creation

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create New Community");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to refresh the community list after adding a new one
    public void refreshCommunities() {
        handleLoadCommunities();
    }
}