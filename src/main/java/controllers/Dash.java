package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import models.User;
import utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Dash implements Initializable {

    private double xOffset=0;
    private double yOffset=0;
    @FXML
    private HBox root;

    @FXML
    private JFXButton btn_logout;
    @FXML
    private BorderPane borderpane;
    @FXML
    private ImageView reduceIcon;
    @FXML
    private JFXButton btn_list;
    @FXML
    private ImageView pdf;
    @FXML
    private JFXButton btn_report;
    @FXML
    private Circle profileImg;

    @FXML
    private Pane paneshow;

    @FXML
    private Label okba;
    @FXML
    private Label dashusername;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeStageDrageable();
        User loggedInUser = SessionManager.getSession(SessionManager.getLastSessionId());
        
        // Set user name
        dashusername.setText(loggedInUser.getNom());
        
        // Try to load profile image
        String photoUrl = loggedInUser.getPhoto();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            try {
                // Try to load from absolute path first
                Image img = new Image("file:" + photoUrl);
                if (img.isError()) {
                    // If that fails, try as a resource
                    img = new Image(getClass().getResource("/" + photoUrl).toExternalForm());
                }
                profileImg.setFill(new ImagePattern(img));
            } catch (Exception e) {
                System.out.println("Could not load profile image: " + e.getMessage());
                // Set a default image or leave as is
            }
        }
        
        okba.setVisible(true);
    }
    @FXML
    private void btn_list_view(ActionEvent event) throws IOException {
        AnchorPane view = FXMLLoader.load(getClass().getResource("/Dashboard.fxml"));
        paneshow.getChildren().setAll(view);
    }

    @FXML
    private void btn_Mailing(ActionEvent event) throws IOException {
        AnchorPane view = FXMLLoader.load(getClass().getResource("/Mail.fxml"));
        paneshow.getChildren().setAll(view);
        okba.setVisible(false);
    }
    @FXML
    private void btn_stat(ActionEvent event) throws IOException {
        AnchorPane view = FXMLLoader.load(getClass().getResource("/Stat.fxml"));
        paneshow.getChildren().setAll(view);
        okba.setVisible(false);
    }


    public void makeStageDrageable(){
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                root.getScene().getWindow().setX(event.getScreenX() - xOffset);
                root.getScene().getWindow().setY(event.getScreenY() - yOffset);
            }

        });
    }
    @FXML
    private void reduceWindow(MouseEvent event) {
        Stage stage = (Stage) reduceIcon.getScene().getWindow();
        stage.setIconified(true);
    }
    @FXML
    private void close_app(MouseEvent event){
        System.exit(0);

    }
    public String sessionId;


    @FXML
    private void logout(ActionEvent event) throws IOException {

        SessionManager.endSession();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainUI.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) btn_logout.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


    public void stats(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Stat.fxml"));
        Parent root = loader.load();

        // Create a new stage
        Stage stage = new Stage();
        stage.setTitle("Statistics"); // Set the title of the new stage

        // Set the scene to the stage
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Show the new stage
        stage.show();

    }




}