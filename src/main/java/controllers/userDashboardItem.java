package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Status;
import models.User;
import services.UserService;
import controllers.Home;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class userDashboardItem implements Initializable {

    @FXML
    private AnchorPane doneAction;
    @FXML
    private Label fullNameLabel;
    @FXML
    private Button banButton;
    @FXML
    private Circle imageCircle;
    @FXML
    private Label roleLabel;
    @FXML
    private Label statuslab;
    private User user;

    public void setFeedBackData(User user) {
        if (user == null) {
            return;
        }
        this.user = user;
        
        // Set user photo with error handling
        try {
            String photoPath = user.getPhoto();
            if (photoPath != null && !photoPath.isEmpty()) {
                photoPath = photoPath.replace("\\", "/");
                Image image = new Image(photoPath, false);
                imageCircle.setFill(new ImagePattern(image));
            } else {
                // Set a default image or placeholder
                imageCircle.setFill(javafx.scene.paint.Color.LIGHTGRAY);
            }
        } catch (Exception e) {
            System.err.println("Error loading user photo: " + e.getMessage());
            imageCircle.setFill(javafx.scene.paint.Color.LIGHTGRAY);
        }

        // Set user information with null checks
        fullNameLabel.setText(user.getNom() != null ? user.getNom() : "N/A");
        roleLabel.setText(user.getRole() != null ? user.getRole() : "User");
        
        // Handle status and button state
        Status userStatus = user.getStatus() != null ? user.getStatus() : Status.ACTIVE;
        updateStatusDisplay(userStatus);
        
        // Set up ban button action
        banButton.setOnAction(event -> handleBanAction());



    }

    @FXML
    public void onDetailsClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/OperationAdmin.fxml"));
        Parent root = fxmlLoader.load();
        OperationAdmin controller = fxmlLoader.getController();
        controller.showUserDetails(user);
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();  // Get the current stage
        stage.setScene(scene);
        stage.show();



    }
    private void updateStatusDisplay(Status status) {
        boolean isActive = status == Status.ACTIVE;
        banButton.setText(isActive ? " Ban user" : " Unban user");
        statuslab.setText(isActive ? "ACTIVE" : "INACTIVE");
        statuslab.setStyle("-fx-text-fill: " + (isActive ? "green" : "red") + ";");
    }

    private void showActionFeedback() {
        doneAction.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1500), e -> doneAction.setVisible(false)));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void handleBanAction() {
        try {
            if (user == null) return;
            Status newStatus = user.getStatus() == Status.ACTIVE ? Status.INACTIVE : Status.ACTIVE;
            user.setStatus(newStatus);
            updateStatusDisplay(newStatus);
            banUser();
        } catch (SQLException e) {
            System.err.println("Error updating user status: " + e.getMessage());
            // Revert UI changes if the database update failed
            updateStatusDisplay(user.getStatus());
        }
    }

    @FXML
    private void banUser() throws SQLException {
        if (user == null) return;
        
        try {
            UserService usr = new UserService();
            usr.modifier(user);
            showActionFeedback();
            System.out.println("User " + (user.getStatus() == Status.ACTIVE ? "unbanned" : "banned"));
        } catch (SQLException e) {
            System.err.println("Database error while updating user status: " + e.getMessage());
            throw e; // Re-throw to be handled by caller
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        doneAction.setVisible(false);



    }
}