package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Technologie;
import services.TechnologieService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AjouterTechnologie {
    private final TechnologieService technologieService = new TechnologieService();

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
    private GridPane gridTech; // Removed scrollPaneTech since it’s not directly used

    @FXML
    private TextField searchFieldTech;

    @FXML
    private Button searchButtonTech;

    public void initialize() {
        // Populate ComboBoxes with options
        // t3.getItems().addAll("Haute", "Moyenne", "Faible");
        // t4.getItems().addAll("Windows", "Linux", "macOS");



        // Set up search functionality
        if (searchFieldTech != null) {
            searchFieldTech.textProperty().addListener((obs, oldValue, newValue) -> filterTechnologies(newValue));
        }
        if (searchButtonTech != null) {
            searchButtonTech.setOnAction(event -> filterTechnologies(searchFieldTech.getText().trim()));
        }

        // Initial display of all technologies
        Platform.runLater(() -> displayTechnologies(technologieService.getAll()));
    }

    @FXML
    void ajouterAction() { // Removed ActionEvent parameter since it’s not used
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
            refreshTechnologies();
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de la technologie : " + e.getMessage());
        }
    }

    private void displayTechnologies(List<Technologie> technologies) {
        gridTech.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Technologie tech : technologies) {
            // Create a white VBox card for the technology
            VBox techCard = new VBox(10); // Spacing for better layout
            techCard.getStyleClass().add("project-card"); // Apply the white card style from CSS

            // Technology name label (black text)
            Label nomLabel = createLabel("Nom: " + (tech.getNom_tech() != null ? tech.getNom_tech() : ""), 14, true);

            // Technology type label (black text)
            Label typeLabel = createLabel("Type: " + (tech.getType_tech() != null ? tech.getType_tech() : ""), 12, false);

            // Technology complexity label (black text)
            Label complexiteLabel = createLabel("Complexité: " + (tech.getComplexite() != null ? tech.getComplexite() : ""), 12, false);

            // Technology documentary label (black text, wrapped)
            Label documentaireLabel = createDocumentaireLabel("Documentaire: " + (tech.getDocumentaire() != null ? tech.getDocumentaire() : "..."));

            // Technology compatibility label (black text)
            Label compatibiliteLabel = createLabel("Compatibilité: " + (tech.getCompatibilite() != null ? tech.getCompatibilite() : ""), 12, false);

            // Buttons (Update, Delete, Listen) in an HBox with pink styling
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER); // Center-align buttons

            Button updateButton = new Button("Update");
            updateButton.getStyleClass().add("action-button"); // Pink button style from CSS
            updateButton.setTooltip(new javafx.scene.control.Tooltip("Update this technology"));
            updateButton.setOnAction(e -> handleUpdate(tech));

            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("action-button"); // Pink button style from CSS
            deleteButton.setTooltip(new javafx.scene.control.Tooltip("Delete this technology"));
            deleteButton.setOnAction(e -> handleDelete(tech));

            Button listenButton = new Button("Listen");
            listenButton.getStyleClass().add("action-button"); // Pink button style from CSS
            listenButton.setTooltip(new javafx.scene.control.Tooltip("Listen to technology details"));
            listenButton.setOnAction(e -> handleListen(tech));

            buttonBox.getChildren().addAll(updateButton, deleteButton, listenButton);

            // Add all attributes to the technology card
            techCard.getChildren().addAll(nomLabel, typeLabel, complexiteLabel, documentaireLabel, compatibiliteLabel, buttonBox);

            // Add the technology card to the GridPane
            gridTech.add(techCard, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
        // Ensure the GridPane is centered
        gridTech.setAlignment(javafx.geometry.Pos.CENTER);
    }

    private void displayTechnologies() {
        try {
            List<Technologie> technologies = technologieService.getAll();
            displayTechnologies(technologies);
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de l'affichage des technologies : " + e.getMessage());
        }
    }

    private void filterTechnologies(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            displayTechnologies();
            return;
        }

        try {
            List<Technologie> allTechnologies = technologieService.getAll();
            List<Technologie> filteredTechnologies = allTechnologies.stream()
                    .filter(tech -> (tech.getNom_tech() != null && tech.getNom_tech().toLowerCase().contains(searchText.toLowerCase())) ||
                            (tech.getType_tech() != null && tech.getType_tech().toLowerCase().contains(searchText.toLowerCase())) ||
                            (tech.getComplexite() != null && tech.getComplexite().toLowerCase().contains(searchText.toLowerCase())) ||
                            (tech.getDocumentaire() != null && tech.getDocumentaire().toLowerCase().contains(searchText.toLowerCase())) ||
                            (tech.getCompatibilite() != null && tech.getCompatibilite().toLowerCase().contains(searchText.toLowerCase())))
                    .collect(Collectors.toList());
            displayTechnologies(filteredTechnologies);
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de la recherche : " + e.getMessage());
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
            stage.showAndWait();
            refreshTechnologies();
        } catch (IOException e) {
            System.err.println("Failed to load UpdateTechnologie.fxml: " + e.getMessage());
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
            refreshTechnologies();
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    private void handleListen(Technologie tech) {
        String textToSpeak = "Nom: " + (tech.getNom_tech() != null ? tech.getNom_tech() : "") +
                ", Type: " + (tech.getType_tech() != null ? tech.getType_tech() : "") +
                ", Complexité: " + (tech.getComplexite() != null ? tech.getComplexite() : "") +
                ", Documentaire: " + (tech.getDocumentaire() != null ? tech.getDocumentaire() : "") +
                ", Compatibilité: " + (tech.getCompatibilite() != null ? tech.getCompatibilite() : "");

        try {
            // Use the same TTS implementation as AfficherProjet
            String pythonExecutable = "python"; // Try "python3" if Python 3.x is needed
            String pythonScriptPath = "C:\\Users\\Mega-Pc\\Desktop\\pi\\Projet\\python\\textToSpeech.py"; // Adjust this path based on your project structure

            java.io.File scriptFile = new java.io.File(pythonScriptPath);
            if (!scriptFile.exists()) {
                System.out.println("Python script not found at: " + pythonScriptPath);
                showAlert("TTS Error", "Python script not found at: " + pythonScriptPath);
                return;
            }

            // Test if Python executable is available
            try {
                ProcessBuilder testPb = new ProcessBuilder(pythonExecutable, "--version");
                testPb.redirectErrorStream(true);
                Process testProcess = testPb.start();
                testProcess.waitFor();
            } catch (IOException | InterruptedException e) {
                System.out.println("Python executable not found: " + pythonExecutable);
                showAlert("TTS Error", "Python executable not found or not in PATH. Please install Python or specify the full path.");
                return;
            }

            ProcessBuilder pb = new ProcessBuilder(pythonExecutable, pythonScriptPath, textToSpeak);
            pb.redirectErrorStream(true);
            pb.directory(scriptFile.getParentFile());
            Process process = pb.start();

            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Python output: " + line);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Python script exited with error code: " + exitCode);
                showAlert("TTS Error", "Python script failed with exit code: " + exitCode);
            } else {
                System.out.println("Text-to-speech completed successfully.");
                showAlert("Succès", "Texte lu avec succès!");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing TTS script: " + e.getMessage());
            showAlert("TTS Error", "An error occurred while running the TTS script: " + e.getMessage());
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

    // Helper method to create labels with common styling
    private Label createLabel(String text, int fontSize, boolean isLarge) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-text-fill: black;");
        label.setTooltip(new Tooltip(text));
        if (!isLarge) {
            label.getStyleClass().add("small");
        }
        return label;
    }

    // Helper method to create documentary label with wrapping
    private Label createDocumentaireLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
        label.setWrapText(true);
        label.setMaxWidth(250.0);
        label.setTooltip(new Tooltip(text));
        label.getStyleClass().add("small");
        return label;
    }
}