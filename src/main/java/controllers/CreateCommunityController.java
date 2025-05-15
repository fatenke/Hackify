package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Communaute;
import services.CommunauteService;
import util.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateCommunityController {

    @FXML
    private ComboBox<HackathonItem> hackathonComboBox;
    @FXML
    private TextField communityNameField;
    @FXML
    private TextArea descriptionField;

    private CommunauteService communauteService;
    private Connection conn;
    private MainLayoutController mainLayoutController;

    // Inner class for Hackathon dropdown items
    private static class HackathonItem {
        private int id;
        private String name;

        public HackathonItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public CreateCommunityController() {
        this.communauteService = new CommunauteService();
        this.conn = MyConnection.getInstance().getConnection();
    }

    @FXML
    public void initialize() {
        populateHackathonComboBox();
    }

    private void populateHackathonComboBox() {
        ObservableList<HackathonItem> hackathons = FXCollections.observableArrayList();
        String sql = "SELECT id, nom FROM hackathon"; // Adjust 'nom' to your column name

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                hackathons.add(new HackathonItem(rs.getInt("id"), rs.getString("nom")));
            }
            hackathonComboBox.setItems(hackathons);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load hackathons: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreate() {
        HackathonItem selectedHackathon = hackathonComboBox.getValue();
        String name = communityNameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (selectedHackathon == null || name.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        Communaute newCommunity = new Communaute(selectedHackathon.getId(), name, description);

        try {
            communauteService.add(newCommunity);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Community created successfully!");

            // Close the window and refresh the main layout
            Stage stage = (Stage) communityNameField.getScene().getWindow();
            stage.close();
            if (mainLayoutController != null) {
                mainLayoutController.refreshCommunities();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create community: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setMainLayoutController(MainLayoutController controller) {
        this.mainLayoutController = controller;
    }
}