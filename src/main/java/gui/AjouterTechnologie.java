package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import services.TechnologieService;
import models.Technologie;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AjouterTechnologie {
    private final TechnologieService technologieService = new TechnologieService();

    @FXML
    private Button b6;

    @FXML
    private TextField t1;

    @FXML
    private TextField t2;

    @FXML
    private ComboBox<String> t3;

    @FXML
    private ComboBox<String> t4;

    @FXML
    private TextField t5;

    @FXML
    private ImageView imageView;

    @FXML
    private ScrollPane scrollPaneTech;

    @FXML
    private GridPane gridTech;

    public void initialize() {
        t3.getItems().addAll("Haute", "Moyenne", "Faible");
        t4.getItems().addAll("Oui", "Non");

        try {
            String imagePath = "/java-logo.png";
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            imageView.setImage(image);
            System.out.println("Image loaded successfully from: " + imagePath);
        } catch (Exception e) {
            System.err.println("Image not found: " + e.getMessage());
            try {
                Image fallbackImage = new Image(getClass().getResource("/images/java_logo.png").toExternalForm());
                imageView.setImage(fallbackImage);
                System.out.println("Fallback image loaded successfully from: /images/java_logo.png");
            } catch (Exception fallbackE) {
                System.err.println("Fallback image not found: " + fallbackE.getMessage());
                imageView.setVisible(false);
                System.err.println("ImageView hidden due to missing images.");
            }
        }

        displayTechnologies();
    }

    @FXML
    void ajouterAction(ActionEvent event) {
        String nom_tech = t1.getText().trim();
        String type_tech = t2.getText().trim();
        String complexite = t3.getValue();
        String compatibilite = t4.getValue();
        String documentaire = t5.getText().trim();

        if (nom_tech.isEmpty()) {
            showAlert("Erreur", "Le nom de la technologie ne peut pas être vide.");
            return;
        }
        if (type_tech.isEmpty()) {
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
        if (documentaire.isEmpty()) {
            showAlert("Erreur", "Le documentaire ne peut pas être vide.");
            return;
        }

        Technologie t = new Technologie(nom_tech, type_tech, complexite, documentaire, compatibilite);

        try {
            technologieService.add(t);
            showAlert("Succès", "Technologie ajoutée avec succès!");
            clearFields();
            displayTechnologies();
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de la technologie : " + e.getMessage());
        }
    }

    private void displayTechnologies() {
        gridTech.getChildren().clear();
        try {
            List<Technologie> technologies = technologieService.getAll();
            int row = 0;
            int col = 0;

            for (Technologie tech : technologies) {
                VBox techBox = new VBox(5);
                techBox.getChildren().add(new Label("Nom: " + tech.getNom_tech()));
                techBox.getChildren().add(new Label("Type: " + tech.getType_tech()));
                techBox.getChildren().add(new Label("Complexité: " + tech.getComplexite()));
                techBox.getChildren().add(new Label("Documentaire: " + tech.getDocumentaire()));
                techBox.getChildren().add(new Label("Compatibilité: " + tech.getCompatibilite()));

                Button updateButton = new Button("Update");
                updateButton.getStyleClass().add("update-button");
                updateButton.setOnAction(e -> handleUpdate(tech));

                Button deleteButton = new Button("Delete");
                deleteButton.getStyleClass().add("delete-button");
                deleteButton.setOnAction(e -> handleDelete(tech));

                Button listenButton = new Button("Listen");
                listenButton.getStyleClass().add("listen-button");
                listenButton.setOnAction(e -> handleListen(tech));

                techBox.getChildren().addAll(updateButton, deleteButton, listenButton);
                gridTech.add(techBox, col, row);

                col++;
                if (col == 3) {
                    col = 0;
                    row++;
                }
            }
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de l'affichage des technologies : " + e.getMessage());
        }
    }

    private void handleUpdate(Technologie tech) {
        System.out.println("Updating technology: " + (tech.getNom_tech() != null ? tech.getNom_tech() : ""));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateTechnologie.fxml"));
            Parent root = loader.load();
            UpdateTechnologie updateController = loader.getController();
            updateController.setTechnologie(tech);
            updateController.setAfficherTechnologieController(this);
            Stage stage = new Stage();
            stage.setTitle("Update Technology");
            stage.setScene(new Scene(root, 400, 400));
            stage.showAndWait(); // Block until dialog is closed
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Failed to load UpdateTechnologie.fxml: " + e.getMessage());
        }
    }

    public void refreshTechnologies() {
        displayTechnologies();
    }

    private void handleDelete(Technologie tech) {
        try {
            technologieService.delete(tech);
            showAlert("Succès", "Technologie supprimée avec succès!");
            displayTechnologies();
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private void handleListen(Technologie tech) {
        String textToSpeak = "Nom: " + tech.getNom_tech() +
                ", Type: " + tech.getType_tech() +
                ", Complexité: " + tech.getComplexite() +
                ", Documentaire: " + tech.getDocumentaire() +
                ", Compatibilité: " + tech.getCompatibilite();

        try {
            showAlert("Fonctionnalité en cours", "Le texte sera lu : " + textToSpeak);
            System.out.println("Speaking: " + textToSpeak);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la lecture du texte : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        t1.clear();
        t2.clear();
        t3.setValue(null);
        t4.setValue(null);
        t5.clear();
    }
}