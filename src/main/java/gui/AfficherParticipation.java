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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AfficherParticipation {
    private final ParticipationService participationService= new ParticipationService();

    @FXML
    private GridPane participationGrid;
    private Hackathon hackathon;

    @FXML
    public void initialize() {
        loadVoirParticipant();
    }
    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
        loadVoirParticipant(); // Charger les participants du hackathon sélectionné
    }

    public void loadVoirParticipant() {
        if (hackathon == null) {
            return;
        }
        ParticipationService ps =new ParticipationService();
        HackathonService hs = new HackathonService();
        List<Participation> participations = ps.getParticipationByHackathon(hackathon.getId_hackathon());
        int row = 2;
        for (Participation participation : participations) {
            Text nameText = new Text("Nom");
            Text EmailText = new Text("Email");
            Text statusText = new Text("Statut");
            Text actionText = new Text("Action");

            participationGrid.add(nameText, 0, 1);
            participationGrid.add(EmailText, 1, 1);
            participationGrid.add(statusText, 2, 1);
            participationGrid.add(actionText, 3, 1);


            Text name = new Text(hackathon.getNom_hackathon());
            Text status = new Text(participation.getStatut());
            status.getStyleClass().add("text");
            participationGrid.add(name, 0, row);
            participationGrid.add(status, 2, row);
            if(Objects.equals(participation.getStatut(), "En attente")){
                Button validerButton = new Button("Valider");
                validerButton.getStyleClass().add("btn-action");
                validerButton.setOnAction(e -> handleValiderParticipation(participation));
                Button refuserButton = new Button("Refuser");
                refuserButton.getStyleClass().add("btn-action");
                HBox buttonsBox=new HBox(validerButton,refuserButton);
                participationGrid.add(buttonsBox, 3, row);
                /*participationGrid.add(refuserButton, 3, row);*/
            }
            row++;
        }
    }
    public void handleValiderParticipation(Participation participation){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Validation");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment valider la participation " );
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            participationService.validerParticipation(participation);
            System.out.println("Participation validée");
            participationGrid.getChildren().clear();
            loadVoirParticipant();
        } else {
            System.out.println("Participation en attente");
        }

    }
    public void handleRefuserParticipation(Participation participation){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de Reject");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment refuser la participation " );
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            participationService.refuserParticipation(participation);
            System.out.println("Participation refusée");
            participationGrid.getChildren().clear();
            loadVoirParticipant();
        } else {
            System.out.println("Participation en attente");
        }

    }

}
