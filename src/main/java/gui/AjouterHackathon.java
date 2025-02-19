package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Hackathon;
import services.HackathonService;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AjouterHackathon {
    private final HackathonService hackathonService= new HackathonService();

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
    private TextArea tf_description;

    @FXML
    private TextField tf_lieu;

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_theme;

    @FXML
    private TextField tf_condition;

    @FXML
    void ajouterHackathon(ActionEvent event) {
        String nom = tf_nom.getText().trim();
        String description = tf_description.getText().trim();
        String theme = tf_theme.getText().trim();
        Integer heureDebut =sp_heure_debut.getValue();
        Integer heureFin = sp_heure_fin.getValue();
        LocalDateTime dateTimeDebut = dp_date_debut.getValue().atTime(LocalTime.of(heureDebut, 0));
        LocalDateTime dateTimeFin = dp_date_fin.getValue().atTime(LocalTime.of(heureFin, 0));
        String lieu = tf_lieu.getText().trim();
        String condition = tf_condition.getText().trim();
        Hackathon h= new Hackathon(nom,description,theme,dateTimeDebut,dateTimeFin,lieu,condition);

            hackathonService.add(h);







    }

}




