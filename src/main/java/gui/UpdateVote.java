package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Vote;
import services.VoteService;

import java.time.LocalDate;

public class UpdateVote {

    private final VoteService ps = new VoteService();

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

    private Vote vote;

    /**
     * Pré-remplit les champs avec les données actuelles du vote.
     */
    public void setVoteData(Vote vote) {
        this.vote = vote;
        TFIdVotant.setText(String.valueOf(vote.getIdVotant()));
        TFIdProjet.setText(String.valueOf(vote.getIdProjet()));
        TFIdHackathon.setText(String.valueOf(vote.getIdHackathon()));
        TFIdEvaluation.setText(String.valueOf(vote.getIdEvaluation()));
        TFValeurVote.setText(String.valueOf(vote.getValeurVote()));
        TFDate.setValue(LocalDate.parse(vote.getDate()));
    }

    /**
     * Met à jour un vote après vérification des entrées.
     */
    @FXML
    void updateVote(ActionEvent event) {
        // Vérification des champs vides
        if (TFIdVotant.getText().isEmpty() || TFIdProjet.getText().isEmpty() ||
                TFIdHackathon.getText().isEmpty() || TFIdEvaluation.getText().isEmpty() ||
                TFValeurVote.getText().isEmpty() || TFDate.getValue() == null) {
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

            // Vérification de la plage de valeur du vote (entre 0 et 5)
            if (valeurVote < 0 || valeurVote > 10) {
                afficherAlerte("Erreur", "La valeur du vote doit être comprise entre 0 et 10.");
                return;
            }

            // Mise à jour des données du vote
            vote.setIdVotant(idVotant);
            vote.setIdProjet(idProjet);
            vote.setIdHackathon(idHackathon);
            vote.setIdEvaluation(idEvaluation);
            vote.setValeurVote(valeurVote);
            vote.setDate(date.toString());

            // Mise à jour en base de données
            ps.update(vote);

            afficherAlerte("Succès", "Le vote a été mis à jour avec succès !");
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de saisie", "Veuillez entrer des valeurs numériques valides.");
        }
    }

    /**
     * Retour à l'affichage des votes.
     */
    @FXML
    void backToAfficherVote(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherVote.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) TFIdVotant.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
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
}
