package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private TextField searchField; // Ensure this is in your FXML if you added it

    @FXML
    private Button cancelaffichage;

    @FXML
    void initialize() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                filterProjects(newValue);

            });
        }

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
            VBox projectBox = new VBox(5);
            projectBox.getStyleClass().add("grid-pane");

            // Project attributes with tooltips
            Label nameLabel = new Label("Name: " + (p.getNom() != null ? p.getNom() : ""));
            nameLabel.setTooltip(new javafx.scene.control.Tooltip("Project Name: " + (p.getNom() != null ? p.getNom() : "")));

            Label statusLabel = new Label("Status: " + (p.getStatut() != null ? p.getStatut() : ""));
            statusLabel.setTooltip(new javafx.scene.control.Tooltip("Project Status: " + (p.getStatut() != null ? p.getStatut() : "")));

            Label priorityLabel = new Label("Priority: " + (p.getPriorite() != null ? p.getPriorite() : ""));
            priorityLabel.setTooltip(new javafx.scene.control.Tooltip("Project Priority: " + (p.getPriorite() != null ? p.getPriorite() : "")));

            Label descLabel = new Label("Description: " + (p.getDescription() != null ? p.getDescription() : "..."));
            descLabel.setTooltip(new javafx.scene.control.Tooltip("Project Description: " + (p.getDescription() != null ? p.getDescription() : "...")));
            descLabel.setWrapText(true);

            Label ressourceLabel = new Label("Ressource: " + (p.getRessource() != null ? p.getRessource() : "..."));
            ressourceLabel.setTooltip(new javafx.scene.control.Tooltip("Project Ressource: " + (p.getRessource() != null ? p.getRessource() : "...")));

            // HBox for Update and Delete buttons
            HBox buttonBox = new HBox(5);
            Button updateButton = new Button("Update");
            updateButton.getStyleClass().add("update-button");
            updateButton.setTooltip(new javafx.scene.control.Tooltip("Update this project"));
            updateButton.setOnAction(e -> handleUpdate(p));

            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setTooltip(new javafx.scene.control.Tooltip("Delete this project"));
            deleteButton.setOnAction(e -> handleDelete(p));

            buttonBox.getChildren().addAll(updateButton, deleteButton);

            // Add TTS button
            Button ttsButton = new Button("Listen");
            ttsButton.getStyleClass().add("tts-button"); // Optional: Add a CSS class for styling
            ttsButton.setTooltip(new javafx.scene.control.Tooltip("Listen to project details"));
            ttsButton.setOnAction(e -> handleTTS(p)); // Call TTS function

            // Add all components to the VBox
            projectBox.getChildren().addAll(nameLabel, statusLabel, priorityLabel, descLabel, ressourceLabel, buttonBox, ttsButton);

            g1.add(projectBox, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void filterProjects(String searchText) {
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
            stage.setScene(new javafx.scene.Scene(root, 400, 400));
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
        // Prepare the text to speak (combine project details)
        String textToSpeak = "Project Details: Name - " + (p.getNom() != null ? p.getNom() : "") +
                ", Status - " + (p.getStatut() != null ? p.getStatut() : "") +
                ", Priority - " + (p.getPriorite() != null ? p.getPriorite() : "") +
                ", Description - " + (p.getDescription() != null ? p.getDescription() : "") +
                ", Resource - " + (p.getRessource() != null ? p.getRessource() : "");

        // Call Python script for TTS
        try {
            // Specify the path to your Python script (adjust the path as needed)
            String pythonScriptPath = "C:\\Users\\Mega-Pc\\Desktop\\pi\\Projet\\python\\textToSpeech.py";
            // Use ProcessBuilder to execute the Python script, passing the text as an argument
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, textToSpeak);
            pb.redirectErrorStream(true); // Combine stdout and stderr
            Process pProcess = pb.start();
            // Optionally, read the output or wait for the process to complete
            pProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error executing TTS script: " + e.getMessage());
            e.printStackTrace();
        }
    }
}