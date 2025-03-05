package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import models.Hackathon;
import models.Participation;
import services.HackathonService;
import services.ParticipationService;


public class HistoriqueParticipation {
    ParticipationService ps =new ParticipationService();
    HackathonService hs = new HackathonService();
    @FXML
    private GridPane participationGrid;

    @FXML
    public void initialize() {
        loadHistorique();
    }

    public void loadHistorique() {
        var participations = ps.getAll();
        int row = 2; // commencer à la ligne 2 (la ligne 1 est pour les titres)
        for (Participation participation : participations) {
            Hackathon hackathon= hs.getHackathonById(participation.getIdHackathon());
            // Ajouter les détails de chaque participation
            /*Text hackathonText = new Text(participation.getHackathon());*/
            Text name = new Text(hackathon.getNom_hackathon());

            Text statusText = new Text(participation.getStatut());
            statusText.getStyleClass().add("text");

            // Ajouter le bouton d'annulation
            Button cancelButton = new Button("Annuler");
            cancelButton.getStyleClass().add("btn-action");
            cancelButton.setOnAction(e -> handleCancelParticipation(participation));

            Button detaillsButton = new Button("voir detaills hackathon");
            detaillsButton.getStyleClass().add("btn-action");
            detaillsButton.setOnAction(e -> detaillsHackathon(hackathon));
            // Ajouter les éléments dans les cellules du GridPane
            participationGrid.add(name, 0, row);
            participationGrid.add(statusText, 2, row);
            participationGrid.add(cancelButton, 3, row);
            participationGrid.add(detaillsButton, 4, row);

            row++;
        }
    }

    public void handleCancelParticipation(Participation participation) {
        if (participation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Annulation");
            alert.setHeaderText("Confirmer l'annulation de la participation");
            alert.setContentText("Êtes-vous sûr de vouloir annuler votre participation au hackathon " );

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    ps.delete(participation);
                    participationGrid.getChildren().clear();
                    loadHistorique();
                }
            });
        }
    }
    public void detaillsHackathon(Hackathon hackathon){

    }

}
