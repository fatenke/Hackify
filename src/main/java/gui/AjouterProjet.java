package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import models.Projet;
import services.ProjetService;

import java.io.IOException;
import java.util.Objects;

public class AjouterProjet {
    private final ProjetService projetService = new ProjetService(); // Use a single instance

    @FXML
    private TextField a1; // For the "nom" field

    @FXML
    private ComboBox<String> a2; // For the "statut" ComboBox

    @FXML
    private ComboBox<String> a3; // For the "priorite" ComboBox

    @FXML
    private TextField a4; // For the "description" field

    @FXML
    private TextField a5; // New TextField for the "ressource" field

    @FXML
    void ajouterAction(ActionEvent event) {
        String nom = a1.getText();
        String statut = a2.getValue(); // Get selected value from ComboBox
        String priorite = a3.getValue(); // Get selected value from ComboBox
        String description = a4.getText();
        String ressource = a5.getText(); // Get the "ressource" value

        // Input validation
        if (nom == null || nom.trim().isEmpty()) {
            showAlert("Erreur", "Le nom du projet ne peut pas être vide.");
            return;
        }
        if (statut == null) {
            showAlert("Erreur", "Veuillez sélectionner un statut.");
            return;
        }
        if (priorite == null) {
            showAlert("Erreur", "Veuillez sélectionner une priorité.");
            return;
        }
        if (ressource == null || ressource.trim().isEmpty()) { // Validate "ressource"
            showAlert("Erreur", "La ressource ne peut pas être vide.");
            return;
        }

        Projet p = new Projet(nom, statut, priorite, description, ressource); // Updated constructor

        try {
            projetService.add(p); // Use the class-level instance
            showAlert("Succès", "Projet ajouté avec succès!");
            clearFields(); // Optional: Clear fields after adding
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AfficherProjet.fxml")));
            a1.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        a1.clear();
        a2.setValue(null);
        a3.setValue(null);
        a4.clear();
        a5.clear(); // Clear the "ressource" field
    }
}