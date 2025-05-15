package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Communaute;
import services.CommunauteService;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class CommunityCardController {
    @FXML
    private Label communityName;
    @FXML
    private Button seeChatsButton;
    @FXML
    private Label communityDescription;
    @FXML
    private Label communityDate;
    @FXML
    private Button deleteButton;

    private int communityId;
    private CommunauteService communauteService;

    public CommunityCardController() {
        this.communauteService = new CommunauteService();
    }

    public void setCommunityDetails(int id, String name, String description, Timestamp date) {
        this.communityId = id;
        communityName.setText(name != null ? name : "Sans nom");
        communityDescription.setText(description != null ? description : "Aucune description");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        communityDate.setText(date != null ? sdf.format(date) : "Aucune date");
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
            showAlert("Erreur", "Erreur lors du chargement des chats : " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette communauté ?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Communaute communaute = communauteService.getById(communityId);
                if (communaute == null) {
                    showAlert("Erreur", "Communauté non trouvée avec l'ID : " + communityId);
                    return;
                }
                communauteService.delete(communaute);
                showAlert("Succès", "Communauté supprimée avec succès !");
                // Optionally, refresh the parent UI instead of closing
                // For now, close the card's parent container (assumed to be a modal or pane)
                deleteButton.getParent().setVisible(false);
                deleteButton.getParent().setManaged(false);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression de la communauté : " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Erreur") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}