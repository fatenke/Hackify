package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NavBar {

    @FXML
    private Button btAfficherHackathon;

    @FXML
    private Button btAjouterHackathon;

    @FXML
    private Button btHistoriqueParticipation;

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


}
