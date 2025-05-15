package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Technologie;
import services.TechnologieService;

public class UpdateTechnologie {
    @FXML
    private TextField nomField;

    @FXML
    private TextField typeField;

    @FXML
    private ComboBox<String> complexiteCombo;

    @FXML
    private TextField documentaireField;

    @FXML
    private ComboBox<String> compatibiliteCombo;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Technologie technologie;
    private final TechnologieService technologieService = new TechnologieService();
    private AjouterTechnologie afficherTechnologieController;

    @FXML
    public void initialize() {
        if (complexiteCombo != null && complexiteCombo.getItems().isEmpty()) {
            complexiteCombo.getItems().addAll("Haute", "Moyenne", "Faible");
        }
        if (compatibiliteCombo != null && compatibiliteCombo.getItems().isEmpty()) {
            compatibiliteCombo.getItems().addAll("Windows", "Linux" ,"macOS");

        }

        saveButton.setOnAction(e -> saveUpdate());
        cancelButton.setOnAction(e -> cancelUpdate());
    }

    public void setTechnologie(Technologie t) {
        this.technologie = t;
        if (t != null) {
            nomField.setText(t.getNom_tech() != null ? t.getNom_tech() : "");
            typeField.setText(t.getType_tech() != null ? t.getType_tech() : "");
            complexiteCombo.setValue(t.getComplexite() != null ? t.getComplexite() : "Moyenne");
            documentaireField.setText(t.getDocumentaire() != null ? t.getDocumentaire() : "");
            compatibiliteCombo.setValue(t.getCompatibilite() != null ? t.getCompatibilite() : "Non");
        }
    }

    public void setAfficherTechnologieController(AjouterTechnologie controller) {
        this.afficherTechnologieController = controller;
    }

    private void saveUpdate() {
        if (technologie == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune technologie sélectionnée pour la mise à jour.");
            return;
        }

        String nom = nomField.getText().trim();
        String type = typeField.getText().trim();
        String complexite = complexiteCombo.getValue();
        String documentaire = documentaireField.getText().trim();
        String compatibilite = compatibiliteCombo.getValue();

        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom de la technologie ne peut pas être vide.");
            return;
        }
        if (type.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le type de la technologie ne peut pas être vide.");
            return;
        }
        if (complexite == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une complexité.");
            return;
        }
        if (documentaire.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le documentaire ne peut pas être vide.");
            return;
        }
        if (compatibilite == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une compatibilité.");
            return;
        }

        technologie.setNom_tech(nom);
        technologie.setType_tech(type);
        technologie.setComplexite(complexite);
        technologie.setDocumentaire(documentaire);
        technologie.setCompatibilite(compatibilite);

        try {
            technologieService.update(technologie);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Technologie mise à jour avec succès !");
            if (afficherTechnologieController != null) {
                afficherTechnologieController.refreshTechnologies();
            } else {
                System.out.println("AjouterTechnologie controller is null!");
            }
            cancelUpdate();
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    private void cancelUpdate() {
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