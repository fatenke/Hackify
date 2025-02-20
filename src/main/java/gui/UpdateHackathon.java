package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Hackathon;
import services.HackathonService;

import java.io.IOException;
import java.time.LocalTime;

public class UpdateHackathon {

    @FXML
    private DatePicker dp_date_debut;

    @FXML
    private DatePicker dp_date_fin;

    @FXML
    private Spinner<Integer> sp_heure_debut;

    @FXML
    private Spinner<Integer> sp_heure_fin;

    @FXML
    private TextField tf_condition;

    @FXML
    private TextField tf_description;

    @FXML
    private TextField tf_lieu;
    @FXML
    private TextField tf_theme;

    @FXML
    private TextField tf_nom;
    private HackathonService hackathonService = new HackathonService();
    private Hackathon hackathon;

    public void setHackathon(Hackathon h) {
        this.hackathon = h;
        tf_nom.setText(h.getNom_hackathon());
        tf_description.setText(h.getDescription());
        tf_lieu.setText(h.getLieu());
        tf_condition.setText(h.getConditions_participation());
        tf_theme.setText(h.getTheme());
        dp_date_debut.setValue(h.getDate_debut().toLocalDate());
        dp_date_fin.setValue(h.getDate_fin().toLocalDate());
        sp_heure_debut.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, h.getDate_debut().getHour()));
        sp_heure_fin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, h.getDate_fin().getHour()));
    }

    @FXML
    void UpdateHackathon(ActionEvent event) {
        if (hackathon == null) {
            System.out.println("Erreur : Aucun hackathon sélectionné pour la mise à jour.");
            return;
        }

        hackathon.setNom_hackathon(tf_nom.getText().trim());
        hackathon.setDescription(tf_description.getText().trim());
        hackathon.setLieu(tf_lieu.getText().trim());
        hackathon.setConditions_participation(tf_condition.getText().trim());
        hackathon.setTheme(tf_theme.getText().trim());
        hackathon.setDate_debut(dp_date_debut.getValue().atTime(LocalTime.of(sp_heure_debut.getValue(), 0)));
        hackathon.setDate_fin(dp_date_fin.getValue().atTime(LocalTime.of(sp_heure_fin.getValue(), 0)));


        hackathonService.update(hackathon);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherHachathon.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tf_nom.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'affichage des hackathons.", ButtonType.OK);
            alert.showAndWait();
        }



    }

}

