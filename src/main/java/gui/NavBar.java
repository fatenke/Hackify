package gui;

import com.jfoenix.controls.JFXButton;
import controllers.OperationUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.User;
import util.SessionManager;

import java.io.IOException;

public class NavBar {

    User loggedInUser = SessionManager.getSession(SessionManager.getLastSessionId());

    @FXML
    private Button btAfficherHackathon;

    @FXML
    private Button btAjouterHackathon;

    @FXML
    private Button btHistoriqueParticipation;

    @FXML
    private JFXButton btn_logout;

    @FXML
    private Pane paneshow;

    @FXML
    void AfficherHackathon(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherHachathon.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btAfficherHackathon.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AjouterHackathon(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterHackathon.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btAjouterHackathon.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void AfficherParticipation(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/HistoriqueParticipation.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btAjouterHackathon.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AfficherCommunaute(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/CommunityCard.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btAjouterHackathon.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void btn_view_profile(MouseEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OperationUser.fxml"));
        Parent root = loader.load();
        OperationUser controller = loader.getController();
        controller.showUserDetails(loggedInUser);
        paneshow.getChildren().setAll(root);

    }

    @FXML
    private void logoutuser(ActionEvent event) throws IOException {

        SessionManager.endSession();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainUI.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) btn_logout.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }


}
