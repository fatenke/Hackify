package gui;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Ressource;
import services.RessourceService;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.event.ActionEvent;

public class RessourceController {
    private final RessourceService ressourceService = new RessourceService();

        @FXML
        private TextField txtId; // ✅ Ajout du champ ID
        @FXML
        private TextField txtTitre;
        @FXML
        private TextField txtType;
        @FXML
        private TextArea txtDescription;
        @FXML
        private DatePicker dpdate_ajout;
        @FXML
        private CheckBox chkValide;

        @FXML
        private TableView<Ressource> tableRessources;
        @FXML
        private TableColumn<Ressource, Integer> colId; // ✅ Ajout de la colonne ID
        @FXML
        private TableColumn<Ressource, String> colTitre;
        @FXML
        private TableColumn<Ressource, String> colType;
        @FXML
        private TableColumn<Ressource, String> colDescription;
        @FXML
        private TableColumn<Ressource, String> colDate;
        @FXML
        private TableColumn<Ressource, Boolean> colValide;

        private ObservableList<Ressource> ressourceList;

        @FXML
        public void initialize() {
            colId.setCellValueFactory(new PropertyValueFactory<>("id")); // ✅ Associer la colonne ID
            colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            colDate.setCellValueFactory(new PropertyValueFactory<>("dateAjout"));
            colValide.setCellValueFactory(new PropertyValueFactory<>("valide"));

            txtId.setDisable(true); // ✅ Désactiver le champ ID pour éviter les modifications manuelles

            loadRessources();

            tableRessources.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    txtId.setText(String.valueOf(newSelection.getId())); // ✅ Remplir automatiquement l'ID
                    txtTitre.setText(newSelection.getTitre());
                    txtType.setText(newSelection.getType());
                    txtDescription.setText(newSelection.getDescription());
                    Date dateAjout = Date.from(dpdate_ajout.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    chkValide.setSelected(newSelection.isValide());
                }
            });
        }
    @FXML
        private void loadRessources() {
            List<Ressource> ressources = ressourceService.recuperer();
            ressourceList = FXCollections.observableArrayList(ressources);
            tableRessources.setItems(ressourceList);
        }

        @FXML
        void ajouterRessource(ActionEvent event) {
            if (txtTitre.getText().isEmpty() || txtType.getText().isEmpty() || txtDescription.getText().isEmpty() || dpdate_ajout.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs");
                return;
            }

            Ressource ressource = new Ressource(txtTitre.getText(), txtType.getText(), txtDescription.getText(), java.sql.Date.valueOf(dpdate_ajout.getValue()), chkValide.isSelected());
            ressourceService.ajouter(ressource);
            loadRessources();
            clearFields();
        }

        @FXML
        void supprimerRessource(ActionEvent event) {
            Ressource selectedRessource = tableRessources.getSelectionModel().getSelectedItem();

            if (selectedRessource == null) {
                showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une ressource à supprimer");
                return;
            }

            ressourceService.supprimer(selectedRessource);
            loadRessources();
            clearFields();
        }

    @FXML
    void modifierRessource(ActionEvent event) {
        // Récupérer la ressource sélectionnée dans la table
        Ressource selectedRessource = tableRessources.getSelectionModel().getSelectedItem();

        // Vérifier si une ressource a été sélectionnée
        if (selectedRessource == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une ressource à modifier");
            return;
        }

        // Vérifier si tous les champs sont remplis
        if (txtTitre.getText().isEmpty() || txtType.getText().isEmpty() || txtDescription.getText().isEmpty() || dpdate_ajout.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs");
            return;
        }

        // Mettre à jour les propriétés de la ressource sélectionnée
        selectedRessource.setTitre(txtTitre.getText());
        selectedRessource.setType(txtType.getText());
        selectedRessource.setDescription(txtDescription.getText());
        selectedRessource.setDateAjout(java.sql.Date.valueOf(dpdate_ajout.getValue()));
        selectedRessource.setValide(chkValide.isSelected());

        // Appeler le service pour mettre à jour la ressource dans la base de données
        ressourceService.modifier(selectedRessource);

        // Rafraîchir la table pour afficher la ressource modifiée
        tableRessources.refresh();

        // Réinitialiser les champs du formulaire
        clearFields();
    }


    private void clearFields() {
            txtId.clear(); // ✅ Effacer l'ID après chaque action
            txtTitre.clear();
            txtType.clear();
            txtDescription.clear();
            dpdate_ajout.setValue(null);
            chkValide.setSelected(false);
        }
        private void showAlert(Alert.AlertType alertType, String title, String content) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        }
    @FXML
    public void openChapitreWindow(ActionEvent event) {
        try {
            // Charger l'interface Chapitre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudChapitre.fxml"));
            Parent chapitreRoot = loader.load();

            // Créer une nouvelle scène et une nouvelle fenêtre pour Chapitre
            Stage chapitreStage = new Stage();
            chapitreStage.setTitle("Gestion des Chapitres");
            chapitreStage.setScene(new Scene(chapitreRoot));
            chapitreStage.show();

            // Fermer la fenêtre actuelle (facultatif)
            // ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre des chapitres.");
        }
    }
    }



