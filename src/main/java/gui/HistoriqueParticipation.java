package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        int row = 2;
        for (Participation participation : participations) {
            Hackathon hackathon= hs.getHackathonById(participation.getIdHackathon());
            Text hackathonText = new Text("Hackathon");
            hackathonText.setStyle("-fx-font-weight: bold;");
            hackathonText.setWrappingWidth(140.26641082763672);

            Text dateDText = new Text("Date début");
            dateDText.setStyle("-fx-font-weight: bold;");
            dateDText.setWrappingWidth(93.66681098937988);
            Text dateFText = new Text("Date fin");
            dateFText.setStyle("-fx-font-weight: bold;");
            dateFText.setWrappingWidth(93.66681098937988);

            Text statusText = new Text("Status");
            statusText.setStyle("-fx-font-weight: bold;");
            statusText.setWrappingWidth(75.06249618530273);

            Text actionText = new Text("Action");
            actionText.setStyle("-fx-font-weight: bold;");
            actionText.setWrappingWidth(138.47308349609375);
            participationGrid.add(hackathonText, 0, 1);
            participationGrid.add(dateDText, 1, 1);
            participationGrid.add(dateFText, 2, 1);
            participationGrid.add(statusText, 3, 1);
            participationGrid.add(actionText, 4, 1);

            Text name = new Text(hackathon.getNom_hackathon());

            Text status = new Text(participation.getStatut());
            status.getStyleClass().add("text");

            Button cancelButton = new Button("Annuler");
            cancelButton.getStyleClass().add("btn-action");
            cancelButton.setOnAction(e -> handleCancelParticipation(participation));

            Button detaillsButton = new Button("voir detaills hackathon");
            detaillsButton.getStyleClass().add("btn-action");
            detaillsButton.setOnAction(e -> detaillsHackathon(hackathon));
            HBox hbox =new HBox(cancelButton,detaillsButton);
            hbox.setSpacing(10);

            participationGrid.add(name, 0, row);
            participationGrid.add(status, 3, row);
            participationGrid.add(hbox, 4, row);


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
