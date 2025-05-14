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
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Status;
import models.User;
import services.UserService;
import java.io.File;
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

    public void setFeedBackData(User user){
        // Set default circle fill color
        imageCircle.setFill(Color.DODGERBLUE);
        
        // Try to load user photo if available
        String photoPath = user.getPhoto();
        if (photoPath != null && !photoPath.trim().isEmpty()) {
            try {
                // Try to load from file system
                File photoFile = new File(photoPath);
                if (photoFile.exists()) {
                    Image img = new Image(photoFile.toURI().toString());
                    if (!img.isError()) {
                        imageCircle.setFill(new ImagePattern(img));
                    }
                }
            } catch (Exception e) {
                System.err.println("Could not load image for user " + user.getNom() + ": " + e.getMessage());
                // Keep default color if image loading fails
            }
        }
        
        fullNameLabel.setText(user.getNom());
        roleLabel.setText(user.getRole());
        this.user = user;
        
        if(user.getStatus().equals(Status.ACTIVE)) {
            banButton.setText(" Ban user");
            statuslab.setText("ACTIVE");
            statuslab.setStyle("-fx-text-fill: green;");
        } else {
            banButton.setText(" Unban user");
            statuslab.setText("INACTIVE");
            statuslab.setStyle("-fx-text-fill: red;");
        }
        
        banButton.setOnAction(__ -> {
            try {
                if(user.getStatus().equals(Status.ACTIVE)) {
                    banButton.setText(" Unban user");
                    statuslab.setText("INACTIVE");
                    statuslab.setStyle("-fx-text-fill: red;");
                }
                else {
                    banButton.setText(" Ban user");
                    statuslab.setText("ACTIVE");
                    statuslab.setStyle("-fx-text-fill: green;");
                }
                banUser();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });



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
    @FXML
    public void banUser() throws SQLException {

        UserService usr = new UserService();
        Status currentUserState = user.getStatus();

        if (currentUserState.equals(Status.ACTIVE)) {
            // User is currently active, so ban the user
            user.setStatus(Status.INACTIVE);
            usr.modifier(user);
            doneAction.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1500), e -> doneAction.setVisible(false)));
            timeline.setCycleCount(1);
            timeline.play();
            System.out.println("User banned");
        } else {
            // User is currently banned, so unban the user
            user.setStatus(Status.ACTIVE);
            usr.modifier(user);
            doneAction.setVisible(true);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1500), e -> doneAction.setVisible(false)));
            timeline.setCycleCount(1);
            timeline.play();
            System.out.println("User unbanned");
        }

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        doneAction.setVisible(false);



    }
}