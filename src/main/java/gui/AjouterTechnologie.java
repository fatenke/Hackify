package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import services.TechnologieService;
import models.Technologie;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AjouterTechnologie {

    private final TechnologieService technologieService = new TechnologieService(); // Single instance of the service

    @FXML
    private Button b6; // The "Ajouter" button

    @FXML
    private TextField t1; // For the "Nom" field

    @FXML
    private TextField t2; // For the "Type" field

    @FXML
    private ComboBox<String> t3; // For the "Complexite" ComboBox (changed from <?> to String for type safety)

    @FXML
    private ComboBox<String> t4; // For the "Compatibilite" ComboBox (changed from <?> to String for type safety)

    @FXML
    private TextField t5; // For the "Documentaire" field

    @FXML
    private ImageView imageView; // For the ImageView (optional, if you want to handle the image dynamically)

    public void initialize() {
        // Populate ComboBoxes with options
        t3.getItems().addAll("Haute", "Moyenne", "Faible"); // Example values for complexite
        t4.getItems().addAll("Oui", "Non"); // Example values for compatibilite

        // Optional: Handle the image loading (if needed, though FXML handles it statically)
        try {
            String imagePath = "/java-logo.png"; // Match this with your FXML path
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            imageView.setImage(image);
            System.out.println("Image loaded successfully from: " + imagePath);
        } catch (Exception e) {
            System.err.println("Image not found: " + e.getMessage());
            // Optionally, set a default image or hide the ImageView
            try {
                // Use a fallback image in the same directory (e.g., /images/java_logo.png)
                Image fallbackImage = new Image(getClass().getResource("/images/java_logo.png").toExternalForm());
                imageView.setImage(fallbackImage);
                System.out.println("Fallback image loaded successfully from: /images/java_logo.png");
            } catch (Exception fallbackE) {
                System.err.println("Fallback image not found: " + fallbackE.getMessage());
                // Hide the ImageView if no image is available
                imageView.setVisible(false);
                System.err.println("ImageView hidden due to missing images.");
            }
        }
    }

    @FXML
    void ajouterAction(ActionEvent event) {
        String nom_tech = t1.getText(); // Nom of the technology
        String type_tech = t2.getText(); // Type of the technology
        String complexite = t3.getValue(); // Selected value from Complexite ComboBox
        String compatibilite = t4.getValue(); // Selected value from Compatibilite ComboBox
        String documentaire = t5.getText(); // Documentaire of the technology

        // Input validation
        if (nom_tech == null || nom_tech.trim().isEmpty()) {
            showAlert("Erreur", "Le nom de la technologie ne peut pas être vide.");
            return;
        }
        if (type_tech == null || type_tech.trim().isEmpty()) {
            showAlert("Erreur", "Le type de la technologie ne peut pas être vide.");
            return;
        }
        if (complexite == null) {
            showAlert("Erreur", "Veuillez sélectionner une complexité.");
            return;
        }
        if (compatibilite == null) {
            showAlert("Erreur", "Veuillez sélectionner une compatibilité.");
            return;
        }
        if (documentaire == null || documentaire.trim().isEmpty()) {
            showAlert("Erreur", "Le documentaire ne peut pas être vide.");
            return;
        }

        // Create a new Technologie object
        Technologie t = new Technologie(nom_tech, type_tech, complexite, documentaire, compatibilite);

        try {
            technologieService.add(t); // Use the class-level instance to add the technology
            showAlert("Succès", "Technologie ajoutée avec succès!");
            clearFields(); // Optional: Clear fields after adding
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout de la technologie : " + e.getMessage());
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper method to clear all fields after successful addition
    private void clearFields() {
        t1.clear();
        t2.clear();
        t3.setValue(null);
        t4.setValue(null);
        t5.clear();
    }
}