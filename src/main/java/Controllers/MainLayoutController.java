package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CommunityCard.fxml"));
                VBox card = loader.load();
                CommunityCardController cardController = loader.getController();
                // Pass the community id, name, and description
                cardController.setCommunityDetails(communaute.getId(), communaute.getNom(), communaute.getDescription());
                communityCardsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
