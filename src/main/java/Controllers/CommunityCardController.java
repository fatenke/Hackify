package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class CommunityCardController {
    @FXML
    public Label communityName;
    @FXML
    public Button seeChatsButton;
    @FXML
    public Label communityDescription;

    // Field for community id
    private int communityId;

    // Updated method to set community details, including id.
    public void setCommunityDetails(int id, String name, String description) {
        this.communityId = id;
        communityName.setText(name);
        communityDescription.setText(description);
    }

    @FXML
    private void handleSeeChats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/afficherChats.fxml"));
            Parent root = loader.load();

            // Pass the community id to th
            // e chat view controller
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
}
