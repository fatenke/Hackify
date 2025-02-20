    package gui;

    import javafx.collections.FXCollections;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Alert;
    import javafx.scene.control.ComboBox;
    import javafx.scene.control.DatePicker;
    import javafx.scene.control.TextField;
    import javafx.stage.Stage;
    import models.Vote;
    import services.EvaluationService;
    import services.VoteService;

    import java.sql.SQLException;
    import java.time.LocalDate;
    import java.util.List;

    public class UpdateVote {

        private final VoteService ps = new VoteService();
        private final EvaluationService evaluationService = new EvaluationService();

        @FXML
        private TextField TFIdVotant;
        @FXML
        private ComboBox<Integer> CBIdProjet;
        @FXML
        private ComboBox<Integer> CBIdHackathon;
        @FXML
        private ComboBox<Integer> CBIdEvaluation;
        @FXML
        private TextField TFValeurVote;
        @FXML
        private DatePicker TFDate;

        private Vote vote;

        @FXML
        public void initialize() {
            loadHackathons();
            loadEvaluations();
            CBIdHackathon.setOnAction(event -> loadProjectsForHackathon());
            TFDate.setValue(LocalDate.now());
        }
        private void loadEvaluations() {
            try {
                List<Integer> evaluationIds = evaluationService.getEvaluationIds();
                CBIdEvaluation.setItems(FXCollections.observableArrayList(evaluationIds));
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de charger les évaluations.");
            }
        }
        private void loadHackathons() {
            try {
                List<Integer> hackathonIds = evaluationService.getHackathonIds();
                CBIdHackathon.setItems(FXCollections.observableArrayList(hackathonIds));
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de charger les hackathons.");
            }
        }

        private void loadProjectsForHackathon() {
            Integer selectedHackathon = CBIdHackathon.getValue();
            if (selectedHackathon != null) {
                try {
                    List<Integer> projectIds = evaluationService.getProjectsByHackathonId(selectedHackathon);
                    CBIdProjet.setItems(FXCollections.observableArrayList(projectIds));
                } catch (SQLException e) {
                    afficherAlerte("Erreur", "Impossible de charger les projets pour ce hackathon.");
                }
            }
        }
        /**
         * Pré-remplit les champs avec les données actuelles du vote.
         */
        public void setVoteData(Vote vote) {
            this.vote = vote;
            TFIdVotant.setText(String.valueOf(vote.getIdVotant()));
            CBIdProjet.setValue(vote.getIdProjet());
            CBIdHackathon.setValue(vote.getIdHackathon());
            CBIdEvaluation.setValue(vote.getIdEvaluation());
            TFValeurVote.setText(String.valueOf(vote.getValeurVote()));
            TFDate.setValue(LocalDate.parse(vote.getDate()));
        }

        /**
         * Met à jour un vote après vérification des entrées.
         */
        @FXML
        void updateVote(ActionEvent event) {
            // Vérification des champs vides
            if (TFIdVotant.getText().isEmpty() || CBIdProjet.getValue() == null ||
                    CBIdHackathon.getValue() == null || CBIdEvaluation.getValue() == null ||
                    TFValeurVote.getText().isEmpty() || TFDate.getValue() == null) {
                afficherAlerte("Erreur de saisie", "Tous les champs doivent être remplis !");
                return;
            }

            try {
                int idVotant = Integer.parseInt(TFIdVotant.getText());
                int idProjet = CBIdProjet.getValue();
                int idHackathon = CBIdHackathon.getValue();
                int idEvaluation = CBIdEvaluation.getValue();
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
