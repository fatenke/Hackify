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
    private Button btAfficherVote;

    @FXML
    private Button btAjouterVote;

    @FXML
    private Button btAjouterEvaluation;

    @FXML
    private Button btAfficherEvaluation;

    @FXML
    private Button btChatbot;

    @FXML
    private Button btAnalyse;

    @FXML
    void AfficherVote(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Affichervote.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btAfficherVote.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AjouterVote(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajoutervote.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btAjouterVote.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AjouterEvaluation(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvaluation.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btAjouterEvaluation.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AfficherEvaluation(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvaluation.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btAfficherEvaluation.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void Chatbot(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GeminiChatbot.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btChatbot.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void Analyse(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ComplexEvaluationAnalysis.fxml"));
        try {
            Parent newContent = loader.load();
            Stage stage = (Stage) btAnalyse.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

