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
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import models.Evaluation;
import models.Vote;
import services.VoteService;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherVote {

    private final VoteService ps = new VoteService();

    @FXML
    private ListView<Vote> listView;

    @FXML
    private TextField searchField;

    private ObservableList<Vote> votes;

    @FXML
    void initialize() {
        votes = FXCollections.observableList(ps.getAll());
        setupListView();
        listView.setItems(votes);
        listView.refresh();
    }

    private void setupListView() {
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Vote vote, boolean empty) {
                super.updateItem(vote, empty);

                if (empty || vote == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Button deleteButton = new Button("Delete");
                    deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                    deleteButton.setOnAction(event -> deleteVote(event, vote));

                    Button updateButton = new Button("Update");
                    updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    updateButton.setOnAction(event -> openUpdatePage(event, vote));

                    HBox hbox = new HBox(10, deleteButton, updateButton);
                    setText(vote.toString());
                    setGraphic(hbox);
                }
            }
        });
    }
    private void loadAllVotes() {
        ObservableList<Vote> votes = FXCollections.observableList(ps.getAll());
        listView.setItems(votes);
    }
    @FXML
    void searchVotes(ActionEvent event) {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            loadAllVotes();// Reload all votes if search is empty
        } else {
            try {
                int projectId = Integer.parseInt(searchText); // Convert search text to an integer
                List<Vote> filteredVotes = votes.stream()
                        .filter(v -> v.getIdProjet() == projectId)
                        .collect(Collectors.toList());

                listView.setItems(FXCollections.observableList(filteredVotes));

                if (filteredVotes.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No results", "No votes found for Project ID: " + projectId);
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid input", "Please enter a valid numeric Project ID.");
            }
        }
    }



    void deleteVote(ActionEvent event, Vote vote) {
        ps.delete(vote);
        votes.remove(vote); // Remove from full list
        listView.getItems().remove(vote); // Remove from filtered view
        listView.refresh();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Vote deleted successfully.");
    }

    void openUpdatePage(ActionEvent event, Vote vote) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateVote.fxml"));
            Parent root = loader.load();
            UpdateVote controller = loader.getController();
            controller.setVoteData(vote);
            listView.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void backToAjouterVote(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVote.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}