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
import javafx.scene.control.Button;

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
        // Add search listener (if searchField exists in FXML)
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                filterProjects(newValue);
            });
        }

        // Fetch and display all projects on the JavaFX Application Thread
        Platform.runLater(() -> displayProjects(projetService.getAll()));
    }

    public void refreshProjects() {
        System.out.println("Refreshing projects in AfficherProjet...");
        // Ensure UI updates happen on the JavaFX Application Thread
        Platform.runLater(() -> {
            List<Projet> projets = projetService.getAll();
            System.out.println("Refreshed projects: " + projets);
            displayProjects(projets);
        });
    }

    private void displayProjects(List<Projet> projets) {
        g1.getChildren().clear(); // Clear existing content

        int col = 0; // Start with column 0
        int row = 0; // Start with row 0

        // Iterate through each project and add it to the GridPane in a 3-column layout
        for (Projet p : projets) {
            // Create a VBox to stack the project's attributes vertically
            VBox projectBox = new VBox(5); // 5px spacing between elements
            projectBox.getStyleClass().add("grid-pane"); // Apply CSS class instead of inline styles

            // Add project attributes to the VBox with tooltips
            Label nameLabel = new Label("Name: " + (p.getNom() != null ? p.getNom() : ""));
            nameLabel.setTooltip(new javafx.scene.control.Tooltip("Project Name: " + (p.getNom() != null ? p.getNom() : "")));

            Label statusLabel = new Label("Status: " + (p.getStatut() != null ? p.getStatut() : ""));
            statusLabel.setTooltip(new javafx.scene.control.Tooltip("Project Status: " + (p.getStatut() != null ? p.getStatut() : "")));

            Label priorityLabel = new Label("Priority: " + (p.getPriorite() != null ? p.getPriorite() : ""));
            priorityLabel.setTooltip(new javafx.scene.control.Tooltip("Project Priority: " + (p.getPriorite() != null ? p.getPriorite() : "")));

            Label descLabel = new Label("Description: " + (p.getDescription() != null ? p.getDescription() : "..."));
            descLabel.setTooltip(new javafx.scene.control.Tooltip("Project Description: " + (p.getDescription() != null ? p.getDescription() : "...")));
            descLabel.setWrapText(true); // Ensure long descriptions wrap

            Label ressourceLabel = new Label("Ressource: " + (p.getRessource() != null ? p.getRessource() : "..."));
            ressourceLabel.setTooltip(new javafx.scene.control.Tooltip("Project Ressource: " + (p.getRessource() != null ? p.getRessource() : "...")));

            // Create an HBox to hold both Update and Delete buttons side by side
            HBox buttonBox = new HBox(5); // 5px spacing between buttons
            Button updateButton = new Button("Update");
            updateButton.getStyleClass().add("update-button"); // Apply CSS style class
            updateButton.setTooltip(new javafx.scene.control.Tooltip("Update this project"));
            updateButton.setOnAction(e -> handleUpdate(p));

            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("delete-button"); // Apply CSS style class
            deleteButton.setTooltip(new javafx.scene.control.Tooltip("Delete this project"));
            deleteButton.setOnAction(e -> handleDelete(p));

            buttonBox.getChildren().addAll(updateButton, deleteButton);

            // Add labels and button box to the VBox
            projectBox.getChildren().addAll(nameLabel, statusLabel, priorityLabel, descLabel, ressourceLabel, buttonBox);

            // Add the VBox to the GridPane
            g1.add(projectBox, col, row);

            // Move to the next column; if at the end of the row, move to the next row
            col++;
            if (col == 3) { // 3 columns per row
                col = 0;
                row++; // Move to the next row for the next set of projects
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

        displayProjects(filteredProjects); // Display filtered projects
    }

    private void handleUpdate(Projet p) {
        // Handle the update logic here (e.g., open a dialog or navigate to an update screen)
        System.out.println("Updating project: " + (p.getNom() != null ? p.getNom() : ""));
        try {
            // Load the UpdateProjet FXML and open it in a new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateProjet.fxml"));
            Parent root = loader.load();

            UpdateProjet updateController = loader.getController();
            updateController.setProjet(p);
            updateController.setAfficherProjetController(this); // Pass this controller to UpdateProjet

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Update Project");
            stage.setScene(new javafx.scene.Scene(root, 400, 400)); // Match the size in UpdateProjet.fxml
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Projet p) {
        // Handle the delete logic here
        try {
            projetService.delete(p); // Delete the project using the service
            System.out.println("Project deleted: " + (p.getNom() != null ? p.getNom() : ""));
            // Refresh the view by reinitializing the GridPane (re-load projects)
            refreshProjects(); // Use the new refresh method
        } catch (Exception e) {
            System.out.println("Error deleting project: " + e.getMessage());
        }
    }


}



