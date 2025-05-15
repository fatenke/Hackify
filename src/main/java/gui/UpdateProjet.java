package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Projet;
import services.ProjetService;

public class UpdateProjet {
    @FXML private TextField nameField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextField descriptionField;
    @FXML private TextField ressourceField;

    // Labels pour afficher les erreurs
    @FXML private Label errorNom, errorStatut, errorPriorite, errorDescription, errorRessource;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Projet projet;
    private final ProjetService projetService = new ProjetService();
    private AfficherProjet afficherProjetController;

    @FXML
    public void initialize() {
        // Remplir les ComboBox si vide
        if (statusCombo.getItems().isEmpty()) {
            statusCombo.getItems().addAll("En cours", "En pause", "Terminé");
        }
        if (priorityCombo.getItems().isEmpty()) {
            priorityCombo.getItems().addAll("Haute", "Moyenne", "Faible");
        }

        saveButton.setOnAction(e -> saveUpdate());
        cancelButton.setOnAction(e -> cancelUpdate());
    }

    public void setProjet(Projet p) {
        this.projet = p;
        if (p != null) {
            nameField.setText(p.getNom());
            statusCombo.setValue(p.getStatut());
            priorityCombo.setValue(p.getPriorite());
            descriptionField.setText(p.getDescription());
            ressourceField.setText(p.getRessource());
        }
    }

    public void setAfficherProjetController(AfficherProjet controller) {
        this.afficherProjetController = controller;
    }

    private void saveUpdate() {
        boolean isValid = true;

        String nom = nameField.getText().trim();
        String statut = statusCombo.getValue();
        String priorite = priorityCombo.getValue();
        String description = descriptionField.getText().trim();
        String ressource = ressourceField.getText().trim();

        // Validation Nom
        if (nom.isEmpty()) {
            errorNom.setText("Champ obligatoire");
            nameField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (!nom.matches("[a-zA-Z\\s]+")) {
            errorNom.setText("Seulement des lettres");
            nameField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorNom.setText("");
            nameField.setStyle("");
        }

        // Validation Statut
        if (statut == null || statut.isEmpty()) {
            errorStatut.setText("Sélectionner un statut");
            isValid = false;
        } else {
            errorStatut.setText("");
        }

        // Validation Priorité
        if (priorite == null || priorite.isEmpty()) {
            errorPriorite.setText("Sélectionner une priorité");
            isValid = false;
        } else {
            errorPriorite.setText("");
        }

        // Validation Description (URL)
        if (description.isEmpty()) {
            errorDescription.setText("Champ obligatoire");
            descriptionField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (!description.startsWith("http://") && !description.startsWith("https://")) {
            errorDescription.setText("URL invalide");
            descriptionField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorDescription.setText("");
            descriptionField.setStyle("");
        }

        // Validation Ressource (URL)
        if (ressource.isEmpty()) {
            errorRessource.setText("Champ obligatoire");
            ressourceField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (!ressource.startsWith("http://") && !ressource.startsWith("https://")) {
            errorRessource.setText("URL invalide");
            ressourceField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorRessource.setText("");
            ressourceField.setStyle("");
        }

        if (!isValid) return;

        // Mise à jour du projet
        projet.setNom(nom);
        projet.setStatut(statut);
        projet.setPriorite(priorite);
        projet.setDescription(description);
        projet.setRessource(ressource);

        try {
            projetService.update(projet);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Projet mis à jour !");
            if (afficherProjetController != null) {
                afficherProjetController.refreshProjects();
            }
            cancelUpdate();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour : " + e.getMessage());
        }
    }

    private void cancelUpdate() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}