package gui;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import models.Chapitre;
import services.ChapitreService;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ListCell;
import services.HuggingFaceService;


public class ChapitreController {

    private final ChapitreService chapitreService = new ChapitreService();
    private final HuggingFaceService huggingFaceService = new HuggingFaceService();

    @FXML
    private TextField idRessourceField;
    @FXML
    private TextField titreField;
    @FXML
    private TextArea contenuField;
    @FXML
    private TextField urlFichierField;
    @FXML
    private TextField formatFichierField;
    @FXML
    private TextField idField;  // Used for editing chapters, can be empty for add

    @FXML
    private Button saveButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button exportPDFButton;

    @FXML
    private ListView<Chapitre> chapitreListView; // ListView to display chapters

    // Méthode pour ajouter un chapitre
    @FXML
    public void addChapitre(ActionEvent event) {
        try {
            Chapitre chapitre = new Chapitre();
            chapitre.setIdRessource(Integer.parseInt(idRessourceField.getText()));
            chapitre.setTitre(titreField.getText());
            chapitre.setContenu(contenuField.getText());
            chapitre.setUrlFichier(urlFichierField.getText());
            chapitre.setFormatFichier(formatFichierField.getText());

            if (chapitreService.validateChapitre(chapitre)) {
                chapitreService.add(chapitre);
                showAlert("Succès", "Chapitre ajouté avec succès !");
                displayChapitreList();
                clearFields();// Refresh the chapter list
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID de la ressource doit être un entier !");
        }
    }

    @FXML
    public void generateChapterContent(ActionEvent event) {
        // 🔹 Récupérer le titre du chapitre (sujet) saisi par l'utilisateur dans titrefield
        String chapterTitle = titreField.getText().trim();

        // 🔹 Vérifier si l'utilisateur a entré un titre
        if (chapterTitle.isEmpty()) {
            contenuField.setText("⚠️ Veuillez entrer un titre avant de générer du contenu.");
            return;
        }

        // 🔹 Construire le prompt dynamique à partir du titre saisi
        String prompt = "Rédige un chapitre académique sur le sujet suivant : '" + chapterTitle + "'. "
                + "Le texte doit être formel, scientifique, bien organisé et structuré.";

        // 🔹 Appel du service pour générer le contenu
        String generatedContent = huggingFaceService.generateChapterContent(prompt);

        // 🔹 Vérification et affichage du résultat
        if (generatedContent.startsWith("Erreur")) {
            System.out.println("Erreur lors de la génération : " + generatedContent);
            contenuField.setText("❌ La génération a échoué. Veuillez réessayer plus tard.");
        } else {
            contenuField.setText(generatedContent);  // 🔹 Affichage du texte généré
        }
    }


    @FXML
    public void exportSelectedChapitreToPDF(ActionEvent event) {
        Chapitre selectedChapitre = chapitreListView.getSelectionModel().getSelectedItem();
        if (selectedChapitre != null) {
            chapitreService.exportToPDF(selectedChapitre);
            showAlert("Succès", "PDF généré avec succès !");
        } else {
            showAlert("Erreur", "Veuillez sélectionner un chapitre !");
        }
    }




    private void clearFields() {
        idRessourceField.clear();
        titreField.clear();
        contenuField.clear();
        urlFichierField.clear();
        formatFichierField.clear();
    }


    // Méthode pour mettre à jour un chapitre
    @FXML
    public void updateChapitre(ActionEvent event) {
        try {
            // Vérifier si un chapitre est sélectionné
            Chapitre selectedChapitre = chapitreListView.getSelectionModel().getSelectedItem();
            if (selectedChapitre == null) {
                showAlert("Erreur", "Veuillez sélectionner un chapitre à modifier !");
                return;
            }

            // Mettre à jour les données du chapitre existant
            selectedChapitre.setIdRessource(Integer.parseInt(idRessourceField.getText().trim()));
            selectedChapitre.setTitre(titreField.getText());
            selectedChapitre.setContenu(contenuField.getText());
            selectedChapitre.setUrlFichier(urlFichierField.getText());
            selectedChapitre.setFormatFichier(formatFichierField.getText());

            // Vérifier les contraintes métier
            if (chapitreService.validateChapitre(selectedChapitre)) {
                chapitreService.update(selectedChapitre);
                showAlert("Succès", "Chapitre mis à jour avec succès !");
                displayChapitreList(); // Rafraîchir la liste des chapitres
            } else {
                showAlert("Erreur", "Les données du chapitre ne sont pas valides !");
            }

        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID de la ressource doit être un nombre entier !");
        }
    }

        @FXML
        private void fillFieldsFromSelection (MouseEvent event){
            Chapitre selectedChapitre = chapitreListView.getSelectionModel().getSelectedItem();
            if (selectedChapitre != null) {
                idField.setText(String.valueOf(selectedChapitre.getId()));
                idRessourceField.setText(String.valueOf(selectedChapitre.getIdRessource()));
                titreField.setText(selectedChapitre.getTitre());
                contenuField.setText(selectedChapitre.getContenu());
                urlFichierField.setText(selectedChapitre.getUrlFichier());
                formatFichierField.setText(selectedChapitre.getFormatFichier());
            }
        }





    // Méthode pour supprimer un chapitre
    @FXML
    public void deleteChapitre(ActionEvent event) {
        try {
            int id = Integer.parseInt(idField.getText());
            Chapitre chapitre = new Chapitre();
            chapitre.setId(id);

            chapitreService.delete(chapitre);
            showAlert("Succès", "Chapitre supprimé avec succès !");
            displayChapitreList();
            clearFields();
            // Refresh the chapter list
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID du chapitre doit être un entier !");
        }
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher tous les chapitres dans le ListView
    private void displayChapitreList() {
        ObservableList<Chapitre> chapitreList = FXCollections.observableArrayList(chapitreService.getAll());
        chapitreListView.setItems(chapitreList);  // Assigner la liste des chapitres

        // Définir comment afficher les chapitres dans la liste (uniquement le titre)
        chapitreListView.setCellFactory(param -> new ListCell<Chapitre>() {
            @Override
            protected void updateItem(Chapitre chapitre, boolean empty) {
                super.updateItem(chapitre, empty);
                if (empty || chapitre == null) {
                    setText(null);
                } else {
                    setText(chapitre.getTitre()); // Afficher seulement le titre
                }
            }
        });
    }



    // Cette méthode pourrait être appelée au démarrage pour afficher les chapitres existants
    @FXML
    public void initialize() {
        displayChapitreList();  // Initialize the list view with existing chapters
    }
}
