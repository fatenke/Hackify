package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Projet;
import services.ProjetService;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    private Button sortByNameBtn;

    @FXML
    private Button sortByStatusBtn;

    @FXML
    private Button sortByPriorityBtn;

    // Variables pour suivre l'ordre de tri (ascendant/descendant)
    private boolean nameAscending = true;
    private boolean statusAscending = true;
    private boolean priorityAscending = true;

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

        // Configuration des boutons de tri
        if (sortByNameBtn != null) {
            sortByNameBtn.setOnAction(e -> sortByName());
            // Appliquer le style programmatiquement
            sortByNameBtn.setStyle("-fx-background-color: #82e9f1; -fx-text-fill: #1e0425; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 6px 12px; -fx-font-size: 12px;");
        }

        if (sortByStatusBtn != null) {
            sortByStatusBtn.setOnAction(e -> sortByStatus());
            // Appliquer le style programmatiquement
            sortByStatusBtn.setStyle("-fx-background-color: #82e9f1; -fx-text-fill: #1e0425; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 6px 12px; -fx-font-size: 12px;");
        }

        if (sortByPriorityBtn != null) {
            sortByPriorityBtn.setOnAction(e -> sortByPriority());
            // Appliquer le style programmatiquement
            sortByPriorityBtn.setStyle("-fx-background-color: #82e9f1; -fx-text-fill: #1e0425; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 6px 12px; -fx-font-size: 12px;");
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
    @FXML
    private void displayAllProjects(javafx.event.ActionEvent event) {
        displayProjects(projetService.getAll());
    }

    private void displayProjects(List<Projet> projets) {
        g1.getChildren().clear();
        int col = 0, row = 0;

        for (Projet p : projets) {
            VBox projectCard = new VBox(10);
            projectCard.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-padding: 15px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10,0,0,2);"
            );

            Label nameLabel = new Label("Nom: " + p.getNom());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #1e0425;");

            Label statusLabel = new Label("Statut: " + p.getStatut());
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e0425;");

            Label priorityLabel = new Label("Priorité: " + p.getPriorite());
            priorityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e0425;");

            Label descLabel = new Label("Description: " + p.getDescription());
            descLabel.setWrapText(true);
            descLabel.setMaxWidth(250);
            descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e0425;");

            Label resourceLabel = new Label("Ressource: " + p.getRessource());
            resourceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1e0425;");

            HBox buttonBox = new HBox(10);
            buttonBox.setStyle("-fx-alignment: center;");

            Button updateButton = new Button("Update");
            updateButton.setStyle("-fx-background-color: #D291BC; -fx-text-fill: white; -fx-background-radius: 5px;");
            updateButton.setOnAction(e -> handleUpdate(p));

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #D291BC; -fx-text-fill: white; -fx-background-radius: 5px;");
            deleteButton.setOnAction(e -> handleDelete(p));

            Button listenButton = new Button("Listen");
            listenButton.setStyle("-fx-background-color: #D291BC; -fx-text-fill: white; -fx-background-radius: 5px;");
            listenButton.setOnAction(e -> handleTTS(p));

            buttonBox.getChildren().addAll(updateButton, deleteButton, listenButton);

            projectCard.getChildren().addAll(
                    nameLabel, statusLabel, priorityLabel,
                    descLabel, resourceLabel, buttonBox
            );

            g1.add(projectCard, col, row);
            if (++col == 3) { col = 0; row++; }
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
            String pythonScriptPath = "C:\\Users\\Mega-Pc\\Desktop\\pi-int\\pi\\Projet\\python\\textToSpeech.py"; // Adjust this path based on your project structure
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
    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}

/**
 * Trie les projets par nom
 */
private void sortByName() {
    List<Projet> projets = projetService.getAll();

    // Tri par nom (en alternant entre ascendant et descendant)
    if (nameAscending) {
        projets.sort(Comparator.comparing(p -> p.getNom() != null ? p.getNom().toLowerCase() : ""));
    } else {
        projets.sort(Comparator.comparing(p -> p.getNom() != null ? p.getNom().toLowerCase() : "", Comparator.reverseOrder()));
    }

    // Inverser pour le prochain clic
    nameAscending = !nameAscending;

    // Mettre à jour l'affichage
    displayProjects(projets);
}

/**
 * Trie les projets par statut
 */
private void sortByStatus() {
    List<Projet> projets = projetService.getAll();

    // Tri par statut (en alternant entre ascendant et descendant)
    if (statusAscending) {
        projets.sort(Comparator.comparing(p -> p.getStatut() != null ? p.getStatut().toLowerCase() : ""));
    } else {
        projets.sort(Comparator.comparing(p -> p.getStatut() != null ? p.getStatut().toLowerCase() : "", Comparator.reverseOrder()));
    }

    // Inverser pour le prochain clic
    statusAscending = !statusAscending;

    // Mettre à jour l'affichage
    displayProjects(projets);
}

/**
 * Trie les projets par priorité
 */
private void sortByPriority() {
    List<Projet> projets = projetService.getAll();

    // Tri par priorité (en alternant entre ascendant et descendant)
    if (priorityAscending) {
        projets.sort(Comparator.comparing(p -> p.getPriorite() != null ? p.getPriorite().toLowerCase() : ""));
    } else {
        projets.sort(Comparator.comparing(p -> p.getPriorite() != null ? p.getPriorite().toLowerCase() : "", Comparator.reverseOrder()));
    }

    // Inverser pour le prochain clic
    priorityAscending = !priorityAscending;

    // Mettre à jour l'affichage
    displayProjects(projets);
}

/**
 * Affiche la page des statistiques par statut
 */
@FXML
void afficherStatistiques(ActionEvent event) {
    try {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/StatistiquesProjet.fxml")));
        g1.getScene().setRoot(root);
    } catch (IOException e) {
        System.out.println("Erreur lors du chargement de StatistiquesProjet.fxml: " + e.getMessage());
        e.printStackTrace();
    }
}}