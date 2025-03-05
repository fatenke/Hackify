package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Projet;
import services.ProjetService;

import java.io.IOException;
import java.util.List;

public class AfficherProjet {
    private ProjetService projetService = new ProjetService();

    @FXML
    private GridPane g1;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button cancelaffichage;

    @FXML
    void initialize() {
        if (searchField != null) {
            // Real-time filtering as the user types
            searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                filterProjects(newValue);
            });
        }

        // Manual search button action
        if (searchButton != null) {
            searchButton.setOnAction(e -> filterProjects(searchField.getText().trim()));
        }

        // Initial display of all projects
        Platform.runLater(() -> displayProjects(projetService.getAll()));
    }

    public void refreshProjects() {
        System.out.println("Refreshing projects in AfficherProjet...");
        Platform.runLater(() -> {
            List<Projet> projets = projetService.getAll();
            System.out.println("Refreshed projects: " + projets);
            displayProjects(projets);
        });
    }

    private void displayProjects(List<Projet> projets) {
        g1.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Projet p : projets) {
            // Create a white VBox card for the project
            VBox projectCard = new VBox(10); // Spacing for better layout
            projectCard.getStyleClass().add("project-card"); // Apply the white card style from CSS

            // Project name label (black text)
            Label nameLabel = new Label("Nom: " + (p.getNom() != null ? p.getNom() : ""));
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;"); // Black text, 14px font
            nameLabel.setTooltip(new javafx.scene.control.Tooltip("Project Name: " + (p.getNom() != null ? p.getNom() : "")));

            // Project status label (black text)
            Label statusLabel = new Label("Statut: " + (p.getStatut() != null ? p.getStatut() : ""));
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;"); // Black text, 12px font
            statusLabel.setTooltip(new javafx.scene.control.Tooltip("Project Status: " + (p.getStatut() != null ? p.getStatut() : "")));

            // Project priority label (black text)
            Label priorityLabel = new Label("Priorité: " + (p.getPriorite() != null ? p.getPriorite() : ""));
            priorityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;"); // Black text, 12px font
            priorityLabel.setTooltip(new javafx.scene.control.Tooltip("Project Priority: " + (p.getPriorite() != null ? p.getPriorite() : "")));

            // Project description label (black text, wrapped)
            Label descLabel = new Label("Description: " + (p.getDescription() != null ? p.getDescription() : "..."));
            descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;"); // Black text, 12px font
            descLabel.setWrapText(true); // Allow text wrapping for description
            descLabel.setMaxWidth(250.0); // Limit width to fit within the card
            descLabel.setTooltip(new javafx.scene.control.Tooltip("Project Description: " + (p.getDescription() != null ? p.getDescription() : "...")));

            // Project resource label (black text)
            Label resourceLabel = new Label("Ressource: " + (p.getRessource() != null ? p.getRessource() : "..."));
            resourceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;"); // Black text, 12px font
            resourceLabel.setTooltip(new javafx.scene.control.Tooltip("Project Resource: " + (p.getRessource() != null ? p.getRessource() : "...")));

            // Project date label (black text, simulating date as in Image 2)
            String dateStr = "2025-03-" + (row * 3 + col + 5) + " 10:00:00"; // Placeholder date format, adjust as needed
            Label dateLabel = new Label(dateStr);
            dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;"); // Black text, 12px font
            dateLabel.getStyleClass().add("date-label"); // Apply date-specific styling
            dateLabel.setTooltip(new javafx.scene.control.Tooltip("Project Date: " + dateStr));

            // Buttons (Update, Delete, Listen) in an HBox with pink styling
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER); // Center-align buttons

            Button updateButton = new Button("Update");
            updateButton.getStyleClass().add("action-button"); // Pink button style from CSS (unchanged)
            updateButton.setTooltip(new javafx.scene.control.Tooltip("Update this project"));
            updateButton.setOnAction(e -> handleUpdate(p));

            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("action-button"); // Pink button style from CSS (unchanged)
            deleteButton.setTooltip(new javafx.scene.control.Tooltip("Delete this project"));
            deleteButton.setOnAction(e -> handleDelete(p));

            Button listenButton = new Button("Listen");
            listenButton.getStyleClass().add("action-button"); // Pink button style from CSS (unchanged)
            listenButton.setTooltip(new javafx.scene.control.Tooltip("Listen to project details"));
            listenButton.setOnAction(e -> handleTTS(p)); // Ensure TTS functionality works

            buttonBox.getChildren().addAll(updateButton, deleteButton, listenButton);

            // Add all attributes to the project card
            projectCard.getChildren().addAll(nameLabel, statusLabel, priorityLabel, descLabel, resourceLabel, dateLabel, buttonBox);

            // Add the project card to the GridPane
            g1.add(projectCard, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void filterProjects(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            displayProjects(projetService.getAll()); // Show all projects if search is empty
            return;
        }

        List<Projet> allProjects = projetService.getAll();
        List<Projet> filteredProjects = new java.util.ArrayList<>();

        for (Projet p : allProjects) {
            if ((p.getNom() != null && p.getNom().toLowerCase().contains(searchText.toLowerCase())) ||
                    (p.getStatut() != null && p.getStatut().toLowerCase().contains(searchText.toLowerCase())) ||
                    (p.getPriorite() != null && p.getPriorite().toLowerCase().contains(searchText.toLowerCase())) ||
                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchText.toLowerCase())) ||
                    (p.getRessource() != null && p.getRessource().toLowerCase().contains(searchText.toLowerCase()))) {
                filteredProjects.add(p);
            }
        }

        displayProjects(filteredProjects);
    }

    private void handleUpdate(Projet p) {
        System.out.println("Updating project: " + (p.getNom() != null ? p.getNom() : ""));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateProjet.fxml"));
            Parent root = loader.load();

            UpdateProjet updateController = loader.getController();
            updateController.setProjet(p);
            updateController.setAfficherProjetController(this);

            Stage stage = new Stage();
            stage.setTitle("Update Project");
            stage.setScene(new Scene(root, 400, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Projet p) {
        try {
            projetService.delete(p);
            System.out.println("Project deleted: " + (p.getNom() != null ? p.getNom() : ""));
            refreshProjects();
        } catch (Exception e) {
            System.out.println("Error deleting project: " + e.getMessage());
        }
    }

    private void handleTTS(Projet p) {
        String textToSpeak = "Project Details: Name - " + (p.getNom() != null ? p.getNom() : "") +
                ", Status - " + (p.getStatut() != null ? p.getStatut() : "") +
                ", Priority - " + (p.getPriorite() != null ? p.getPriorite() : "") +
                ", Description - " + (p.getDescription() != null ? p.getDescription() : "") +
                ", Resource - " + (p.getRessource() != null ? p.getRessource() : "");

        try {
            // Verify if Python is in PATH or use full path to Python executable
            String pythonExecutable = "python"; // Try "python3" if Python 3.x is needed
            // Alternatively, use full path: String pythonExecutable = "C:\\Python39\\python.exe";

            // Use a relative or absolute path to the Python script
            String pythonScriptPath = "C:\\Users\\Mega-Pc\\Desktop\\pi\\Projet\\python\\textToSpeech.py"; // Adjust this path based on your project structure
            // Or use an absolute path: String pythonScriptPath = "C:\\Users\\Mega-Pc\\Desktop\\pi\\Projet\\python\\textToSpeech.py";

            // Check if the script file exists
            java.io.File scriptFile = new java.io.File(pythonScriptPath);
            if (!scriptFile.exists()) {
                System.out.println("Python script not found at: " + pythonScriptPath);
                showErrorAlert("TTS Error", "Python script not found at: " + pythonScriptPath);
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
                showErrorAlert("TTS Error", "Python executable not found or not in PATH. Please install Python or specify the full path.");
                return;
            }

            // Create ProcessBuilder with the Python script and text to speak
            ProcessBuilder pb = new ProcessBuilder(pythonExecutable, pythonScriptPath, textToSpeak);
            pb.redirectErrorStream(true); // Combine stdout and stderr
            pb.directory(scriptFile.getParentFile()); // Set working directory to script location

            // Start the process and wait for it to complete
            Process process = pb.start();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Python output: " + line); // Log Python script output
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Python script exited with error code: " + exitCode);
                showErrorAlert("TTS Error", "Python script failed with exit code: " + exitCode);
            } else {
                System.out.println("Text-to-speech completed successfully.");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing TTS script: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("TTS Error", "An error occurred while running the TTS script: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String content) {
        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
}