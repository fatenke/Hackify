package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import models.Vote;
import services.VoteService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterVote {
    private final VoteService voteService = new VoteService();

    @FXML
    private TextField TFIdVotant;

    @FXML
    private TextField TFIdProjet;

    @FXML
    private TextField TFIdHackathon;

    @FXML
    private TextField TFIdEvaluation;

    @FXML
    private TextField TFValeurVote;

    @FXML
    private DatePicker TFDate;

    @FXML
    void ajouter(ActionEvent event) {
        // Vérification des champs vides
        if (TFIdVotant.getText().isEmpty() || TFIdProjet.getText().isEmpty() || TFIdHackathon.getText().isEmpty() ||
                TFIdEvaluation.getText().isEmpty() || TFValeurVote.getText().isEmpty() || TFDate.getValue() == null) {
            afficherAlerte("Erreur de saisie", "Tous les champs doivent être remplis !");
            return;
        }

        try {
            int idVotant = Integer.parseInt(TFIdVotant.getText());
            int idProjet = Integer.parseInt(TFIdProjet.getText());
            int idHackathon = Integer.parseInt(TFIdHackathon.getText());
            int idEvaluation = Integer.parseInt(TFIdEvaluation.getText());
            float valeurVote = Float.parseFloat(TFValeurVote.getText());
            LocalDate date = TFDate.getValue();

            // Vérification des valeurs positives
            if (idVotant <= 0 || idProjet <= 0 || idHackathon <= 0 || idEvaluation <= 0) {
                afficherAlerte("Erreur", "Les identifiants doivent être des nombres positifs.");
                return;
            }

            // Vérification de la plage des votes (0-10)
            if (valeurVote < 0 || valeurVote > 10) {
                afficherAlerte("Erreur", "La valeur du vote doit être comprise entre 0 et 10.");
                return;
            }

            // Création de l'objet Vote et ajout dans la base
            Vote p = new Vote(valeurVote, date.toString(), idEvaluation, idVotant, idProjet, idHackathon);
            voteService.add(p);

            afficherAlerte("Succès", "Le vote a été ajouté avec succès !");
            viderChamps();

        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie", "Veuillez entrer des valeurs numériques valides.");
        }
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherVote.fxml"));
            TFIdVotant.getScene().setRoot(root);
        } catch (IOException e) {
            afficherAlerte("Erreur", "Impossible d'afficher la fenêtre.");
        }
    }

    /**
     * Affiche une alerte avec un message personnalisé.
     */
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Vide tous les champs après l'ajout.
     */
    private void viderChamps() {
        TFIdVotant.clear();
        TFIdProjet.clear();
        TFIdHackathon.clear();
        TFIdEvaluation.clear();
        TFValeurVote.clear();
        TFDate.setValue(null);
    }
}
