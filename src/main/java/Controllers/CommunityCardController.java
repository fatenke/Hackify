package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Communaute;
import services.CommunauteService;

import java.io.IOException;
import java.sql.Timestamp;

public class CommunityCardController {
    @FXML
    public Label communityName;
    @FXML
    public Button seeChatsButton;
    @FXML
    public Label communityDescription;
    @FXML
    public Label communityDate;
    @FXML
    private Button deleteButton;

    // Field for community id
    private int communityId;

    // Service instance
    private CommunauteService communauteService;

    public CommunityCardController() {
        this.communauteService = new CommunauteService();
    }

    // Updated method to set community details, including id, name, description, and date
    public void setCommunityDetails(int id, String name, String description, Timestamp date) {
        this.communityId = id;
        communityName.setText(name);
        communityDescription.setText(description);
        communityDate.setText(date != null ? date.toString() : "No Date"); // Convert Timestamp to String
    }

    @FXML
    private void handleSeeChats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/afficherChats.fxml"));
            Parent root = loader.load();

            AfficherChatsController controller = loader.getController();
            controller.setCurrentCommunityId(communityId);

            Scene scene = new Scene(root);
            Stage stage = (Stage) seeChatsButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        try {
            Communaute communaute = new Communaute(communityId);
            communauteService.delete(communaute);

            Stage stage = (Stage) deleteButton.getScene().getWindow();
            stage.close(); // Closes the card window
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deleting community: " + e.getMessage());
        }
    }
}