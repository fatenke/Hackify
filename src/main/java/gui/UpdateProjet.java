package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import models.Projet;
import services.ProjetService;
import javafx.stage.Stage;

import java.io.IOException;

public class UpdateProjet {
    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private ComboBox<String> priorityCombo;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField ressourceField; // New field for ressource

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Projet projet;
    private final ProjetService projetService = new ProjetService();
    private AfficherProjet afficherProjetController; // Reference to AfficherProjet controller

    @FXML
    public void initialize() {
        // Initialize ComboBox options (already set in FXML, but this ensures consistency)
        if (statusCombo != null && statusCombo.getItems().isEmpty()) {
            statusCombo.getItems().addAll("En cours", "En pause", "Terminé");
        }
        if (priorityCombo != null && priorityCombo.getItems().isEmpty()) {
            priorityCombo.getItems().addAll("Haute", "Moyenne", "Faible");
        }

        // Handle Save button action
        saveButton.setOnAction(e -> saveUpdate());

        // Handle Cancel button action
        cancelButton.setOnAction(e -> cancelUpdate());
    }

    public void setProjet(Projet p) {
        this.projet = p;
        if (p != null) {
            nameField.setText(p.getNom() != null ? p.getNom() : "");
            statusCombo.setValue(p.getStatut() != null ? p.getStatut() : "En cours"); // Default to "En cours" if null
            priorityCombo.setValue(p.getPriorite() != null ? p.getPriorite() : "Moyenne"); // Default to "Moyenne" if null
            descriptionField.setText(p.getDescription() != null ? p.getDescription() : "");
            ressourceField.setText(p.getRessource() != null ? p.getRessource() : ""); // Set ressource value
        }
    }

    public void setAfficherProjetController(AfficherProjet controller) {
        System.out.println("Setting AfficherProjet controller: " + (controller != null));
        this.afficherProjetController = controller;
    }

    private void saveUpdate() {
        if (projet == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun projet sélectionné pour la mise à jour.");
            return;
        }

        // Validate input
        String nom = nameField.getText().trim();
        String statut = statusCombo.getValue();
        String priorite = priorityCombo.getValue();
        String description = descriptionField.getText().trim();
        String ressource = ressourceField.getText().trim();

        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom du projet ne peut pas être vide.");
            return;
        }
        if (statut == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un statut.");
            return;
        }
        if (priorite == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une priorité.");
            return;
        }
        if (ressource.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La ressource ne peut pas être vide.");
            return;
        }

        // Update the projet object with new values
        projet.setNom(nom);
        projet.setStatut(statut);
        projet.setPriorite(priorite);
        projet.setDescription(description);
        projet.setRessource(ressource); // Update ressource

        // Save the updated project using the service
        try {
            projetService.update(projet);
            System.out.println("Project updated successfully: " + projet.getNom());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet mis à jour avec succès !");
            if (afficherProjetController != null) {
                System.out.println("Attempting to refresh AfficherProjet...");
                afficherProjetController.refreshProjects(); // Refresh the AfficherProjet view
            } else {
                System.out.println("AfficherProjet controller is null!");
            }
            cancelUpdate(); // Close the window after successful update
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour du projet : " + e.getMessage());
        }
    }

    private void cancelUpdate() {
        // Close the stage
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}