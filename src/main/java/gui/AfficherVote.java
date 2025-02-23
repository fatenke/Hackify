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
import models.Vote;
import services.VoteService;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

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

    @FXML
    void searchVotes(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            listView.setItems(votes);
        } else {
            ObservableList<Vote> filteredVotes = votes.filtered(v -> String.valueOf(v.getIdProjet()).equals(searchText));
            listView.setItems(filteredVotes);
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
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText("Votes Report");
            contentStream.newLine();

            for (Vote vote : listView.getItems()) {
                contentStream.showText(vote.toString());
                contentStream.newLine();
            }
            contentStream.endText();
            contentStream.close();
            document.save("votes_report.pdf");
            document.close();
            showAlert(Alert.AlertType.INFORMATION, "Success", "PDF generated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}