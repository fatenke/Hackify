package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import models.Hackathon;
import services.HackathonService;
import javafx.scene.control.Label;

import java.util.List;

public class AfficherHachathon {
    private final HackathonService hackathonService= new HackathonService();

    @FXML
    private GridPane gp_hackathon;
    @FXML
    void initialize() {
        List<Hackathon> hackathons = hackathonService.getAll();
        /*gp_hackathon.add(new Label("Nom"), 0, 0);
        gp_hackathon.add(new Label("Description"), 1, 0);

        int row = 1; // Commence à la ligne 1 (ligne 0 est pour les en-têtes)

        for (Hackathon h : hackathons) {
            gp_hackathon.add(new Label(h.getNom_hackathon()), 0, row); // Colonne 0 : Nom du hackathon
            gp_hackathon.add(new Label(h.getDescription()), 1, row); // Colonne 1 : Description
            row++;
        }*/
        int columns = 3; // Nombre de colonnes dans la grille
        int row = 0, col = 0;

        for (Hackathon h : hackathons) {
            Label hackathonLabel = new Label(h.getNom_hackathon() + "\n" + h.getDescription());
            hackathonLabel.setStyle("-fx-border-color: black; -fx-padding: 10px; -fx-alignment: center;");

            gp_hackathon.add(hackathonLabel, col, row); // Ajouter dans la grille

            col++; // Passer à la colonne suivante
            if (col == columns) { // Si on atteint la 3ème colonne, passer à la ligne suivante
                col = 0;
                row++;
            }
        }
    }


    @FXML
    void DetailsHackathon(ActionEvent event) {

    }

}

