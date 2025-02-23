package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import models.Evaluation;
import services.EvaluationService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherEvaluation {

    private final EvaluationService ps = new EvaluationService();

    @FXML
    private ListView<Evaluation> listView;
    @FXML
    private TextField searchField;

    @FXML
    void initialize() {
        ObservableList<Evaluation> evaluations = FXCollections.observableList(ps.getAll());
        loadAllEvaluations();
        // Set the cell factory to display evaluations with Delete and Update buttons
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Evaluation evaluation, boolean empty) {
                super.updateItem(evaluation, empty);

                if (empty || evaluation == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create Delete button
                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> deleteEvaluation(event, evaluation));

                    // Create Update button
                    Button updateButton = new Button("Update");
                    updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    updateButton.setOnAction(event -> openUpdatePage(event, evaluation));

                    // Create an HBox to hold both buttons
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.getChildren().addAll(deleteButton, updateButton);

                    setText(evaluation.toString());  // Display evaluation details
                    setGraphic(hbox);
                }
            }
        });

        // Set ListView items
        listView.setItems(evaluations);
    }
    private void loadAllEvaluations() {
        ObservableList<Evaluation> evaluations = FXCollections.observableList(ps.getAll());
        listView.setItems(evaluations);
    }
    // Delete Evaluation method
    void deleteEvaluation(ActionEvent event, Evaluation evaluation) {

            ps.delete(evaluation);
            listView.getItems().remove(evaluation); // Remove item from ListView
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Evaluation deleted successfully.");
            alert.showAndWait();

    }

    // Open Update Evaluation Page
    void openUpdatePage(ActionEvent event, Evaluation evaluation) {
        try {
            // Load the update page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateEvaluation.fxml"));
            Parent root = loader.load();

            // Get the controller for the Update page
            UpdateEvaluation controller = loader.getController();
            // Pass the current Evaluation object to the controller
            controller.setEvaluationData(evaluation);

            // Replace the current scene with the update page
            listView.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    @FXML
    void backToAjouterEvaluation(ActionEvent event) {
        try {
            // Load the AjouterEvaluation.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvaluation.fxml"));
            Parent root = loader.load();

            // Create a new scene and set it to the current stage
            Stage stage = (Stage) listView.getScene().getWindow();  // Get the current stage
            stage.setScene(new Scene(root));  // Set the new scene (AjouterEvaluation.fxml)
            stage.show();  // Show the stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void generatePdf(ActionEvent event) {
        try {
            // Create a new PDF document
            PDDocument document = new PDDocument();

            // Create the first page
            PDPage page = new PDPage();
            document.addPage(page);

            // Prepare the content stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);  // Use Helvetica font
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(25, 750); // Start position

            // Write the title
            contentStream.showText("Evaluations Report");
            contentStream.newLine();

            // Set the initial Y position
            float yPosition = 730;

            // Iterate through the evaluations and add them to the PDF
            for (Evaluation evaluation : listView.getItems()) {
                // Check if the Y position is about to go off the page
                if (yPosition < 50) {
                    // Create a new page and reset Y position
                    page = new PDPage();
                    document.addPage(page);
                    contentStream.close();
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);  // Use Helvetica font
                    contentStream.setLeading(14.5f);
                    contentStream.newLineAtOffset(25, 750);
                    yPosition = 730;  // Reset Y position for the new page
                }

                // Write evaluation details
                contentStream.showText("ID: " + evaluation.getId());
                contentStream.newLine();
                contentStream.showText("Jury ID: " + evaluation.getIdJury());
                contentStream.newLine();
                contentStream.showText("Project ID: " + evaluation.getIdProjet());
                contentStream.newLine();
                contentStream.showText("Hackathon ID: " + evaluation.getIdHackathon());
                contentStream.newLine();
                contentStream.showText("Technical Note: " + evaluation.getNoteTech());
                contentStream.newLine();
                contentStream.showText("Innovative Note: " + evaluation.getNoteInnov());
                contentStream.newLine();
                contentStream.showText("Date: " + evaluation.getDate());
                contentStream.newLine();
                contentStream.newLine();

                // Adjust the Y position after writing content
                yPosition -= 90;  // Decrease the Y position to avoid overlapping
            }

            // End the content stream correctly
            contentStream.endText();
            contentStream.close();

            // Save the document to a file
            document.save("evaluations_report.pdf");
            document.close();

            // Inform the user that the PDF was generated
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("PDF generated successfully!");
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("An error occurred while generating the PDF.");
            alert.showAndWait();
        }
    }

    public void goToAnalyse(ActionEvent actionEvent) {
        try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ComplexEvaluationAnalysis.fxml"));
        Parent root = loader.load();

        // Create a new scene and set it to the current stage
        Stage stage = (Stage) listView.getScene().getWindow();  // Get the current stage
        stage.setScene(new Scene(root));  // Set the new scene (AjouterEvaluation.fxml)
        stage.show();  // Show the stage
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    @FXML
    void searchByProjectId(ActionEvent event) {
        String projectIdText = searchField.getText().trim();

        if (projectIdText.isEmpty()) {
            loadAllEvaluations(); // Reload all evaluations if search is empty
        } else {
            try {
                int projectId = Integer.parseInt(projectIdText);
                List<Evaluation> filteredEvaluations = ps.getAll().stream()
                        .filter(e -> e.getIdProjet() == projectId)
                        .collect(Collectors.toList());

                listView.setItems(FXCollections.observableList(filteredEvaluations));

                if (filteredEvaluations.isEmpty()) {
                    showAlert("No results", "No evaluations found for Project ID: " + projectId);
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid input", "Please enter a valid numeric Project ID.");
            }
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

