package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Hackathon;
import services.HackathonService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class AjouterHackathon {
    private final HackathonService hackathonService= new HackathonService();
    @FXML
    private ComboBox<?> cbTypeParticipation;

    @FXML
    private DatePicker dp_date_debut;

    @FXML
    private DatePicker dp_date_fin;

    @FXML
    private Spinner<Integer> sp_heure_debut;

    @FXML
    private Spinner<Integer> sp_heure_fin;

    @FXML
    public void initialize() {
        // Initialisation des spinners pour les heures (0-23)
        sp_heure_debut.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        sp_heure_fin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
    }

    @FXML
    private TextField tbNbrMax;

    @FXML
    private TextArea tf_description;

    @FXML
    private TextField tf_lieu;

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_theme;

    @FXML
    void ajouterHackathon(ActionEvent event) {
        String nom = tf_nom.getText().trim();
        String description = tf_description.getText().trim();
        String theme = tf_theme.getText().trim();
        Integer heureDebut =sp_heure_debut.getValue();
        Integer heureFin = sp_heure_fin.getValue();
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime dateTimeDebut = dp_date_debut.getValue().atTime(LocalTime.of(heureDebut, 0));
        if (dateTimeDebut.isBefore(maintenant)) {
            afficherAlerte("Date invalide", "La date de début doit être après la date et l'heure actuelles !");
            return;
        }
        LocalDateTime dateTimeFin = dp_date_fin.getValue().atTime(LocalTime.of(heureFin, 0));
        if (dateTimeDebut.isAfter(dateTimeFin)) {
            afficherAlerte("Date invalide", "La date de début doit être avant la date de fin !");
            return;
        }
        if (dp_date_debut.getValue() == null || dp_date_fin.getValue() == null) {
            afficherAlerte("Date invalide", "Veuillez sélectionner les dates de début et de fin.");
            return;
        }
        String lieu = tf_lieu.getText().trim();
        String typeParticipation = (String) cbTypeParticipation.getValue();
        int maxParticipants ;
        try {
            maxParticipants = Integer.parseInt(tbNbrMax.getText().trim());
            if (maxParticipants <= 0) {
                afficherAlerte("Nombre de participants invalide", "Le nombre de participants doit être un entier positif.");
                return;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Format invalide", "Veuillez entrer un nombre valide pour les participants.");
            return;
        }

        if (nom.isEmpty() || description.isEmpty() || theme.isEmpty() || lieu.isEmpty() || typeParticipation == null) {
            afficherAlerte("Champs obligatoires", "Veuillez remplir tous les champs avant de continuer.");
            return;
        }
        Hackathon h= new Hackathon(nom,description,theme,dateTimeDebut,dateTimeFin,lieu,maxParticipants);
        if (confirmerAction("Confirmer l'ajout", "Voulez-vous vraiment ajouter ce hackathon ?")) {
            hackathonService.add(h);
            afficherAlerteSucces("Ajout réussi", "Le hackathon a été ajouté avec succès !");
        }
        /*hackathonService.add(h);*/
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #e86371; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-family: 'Comic Sans MS';");
        alert.showAndWait();
    }
    private void afficherAlerteSucces(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher une alerte de confirmation
    private boolean confirmerAction(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}



