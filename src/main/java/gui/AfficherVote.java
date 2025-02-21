package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import models.Vote;
import services.VoteService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.sql.SQLException;

public class AfficherVote {

    private final VoteService ps = new VoteService();

    @FXML
    private ListView<Vote> listView;

    @FXML
    void initialize() {
        ObservableList<Vote> votes = FXCollections.observableList(ps.getAll());


        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Vote vote, boolean empty) {
                super.updateItem(vote, empty);

                if (empty || vote == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create Delete button
                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> deleteVote(event, vote));

                    // Create Update button
                    Button updateButton = new Button("Update");
                    updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    updateButton.setOnAction(event -> openUpdatePage(event, vote));

                    // Create an HBox to hold both buttons
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.getChildren().addAll(deleteButton, updateButton);

                    setText(vote.toString());
                    setGraphic(hbox);
                }
            }
        });

        // Set ListView items
        listView.setItems(votes);
    }


    void deleteVote(ActionEvent event, Vote vote) {

        ps.delete(vote);
        listView.getItems().remove(vote); // Remove item from ListView
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText("Vote deleted successfully.");
        alert.showAndWait();

    }


    void openUpdatePage(ActionEvent event, Vote vote) {
        try {
            // Load the update page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateVote.fxml"));
            Parent root = loader.load();

            // Get the controller for the Update page
            UpdateVote controller = loader.getController();

            controller.setVoteData(vote);

            // Replace the current scene with the update page
            listView.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    @FXML
    void backToAjouterVote(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVote.fxml"));
            Parent root = loader.load();

            // Create a new scene and set it to the current stage
            Stage stage = (Stage) listView.getScene().getWindow();  // Get the current stage
            stage.setScene(new Scene(root));
            stage.show();  // Show the stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void generateVotePdf(ActionEvent event) {
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
            contentStream.showText("Votes Report");
            contentStream.newLine();

            // Set the initial Y position
            float yPosition = 730;

            // Iterate through the votes and add them to the PDF
            for (Vote vote : listView.getItems()) {
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

                // Write vote details
                contentStream.showText("Vote ID: " + vote.getId());
                contentStream.newLine();
                contentStream.showText("Evaluation ID: " + vote.getIdEvaluation());
                contentStream.newLine();
                contentStream.showText("Voter ID: " + vote.getIdVotant());
                contentStream.newLine();
                contentStream.showText("Project ID: " + vote.getIdProjet());
                contentStream.newLine();
                contentStream.showText("Hackathon ID: " + vote.getIdHackathon());
                contentStream.newLine();
                contentStream.showText("Vote Value: " + vote.getValeurVote());
                contentStream.newLine();
                contentStream.showText("Date: " + vote.getDate());
                contentStream.newLine();
                contentStream.newLine();

                // Adjust the Y position after writing content
                yPosition -= 90;  // Decrease the Y position to avoid overlapping
            }

            // End the content stream correctly
            contentStream.endText();
            contentStream.close();

            // Save the document to a file
            document.save("votes_report.pdf");
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
}

