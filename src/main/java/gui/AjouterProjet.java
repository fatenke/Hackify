package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import models.Projet;
import services.ProjetService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class AjouterProjet {
    private final ProjetService projetService = new ProjetService();

    // Champs FXML
    @FXML private TextField a1; // Nom
    @FXML private ComboBox<String> a2; // Statut
    @FXML private ComboBox<String> a3; // Priorité
    @FXML private TextField a4; // Description
    @FXML private TextField a5; // Ressource

    // Labels pour afficher les erreurs
    @FXML private Label errorNom, errorStatut, errorPriorite, errorDescription, errorRessource;

    @FXML private Button listenNom, listenStatut, listenPriorite, listenDescription, listenRessource;

    @FXML
    void ajouterAction(ActionEvent event) {
        String nom = a1.getText().trim();
        String statut = a2.getValue();
        String priorite = a3.getValue();
        String description = a4.getText().trim();
        String ressource = a5.getText().trim();

        boolean isValid = true;

        // Validation Nom (lettres uniquement)
        if (nom.isEmpty()) {
            errorNom.setText("Champ obligatoire");
            a1.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (!nom.matches("[a-zA-Z\\s]+")) {
            errorNom.setText("Seulement des lettres");
            a1.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorNom.setText("");
            a1.setStyle("");
        }

        // Validation Statut
        if (statut == null || statut.isEmpty()) {
            errorStatut.setText("Sélectionner un statut");
            isValid = false;
        } else {
            errorStatut.setText("");
        }

        // Validation Priorité
        if (priorite == null || priorite.isEmpty()) {
            errorPriorite.setText("Sélectionner une priorité");
            isValid = false;
        } else {
            errorPriorite.setText("");
        }

        // Validation Description (URL)
        if (description.isEmpty()) {
            errorDescription.setText("Champ obligatoire");
            a4.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (!description.startsWith("http://") && !description.startsWith("https://")) {
            errorDescription.setText("URL invalide");
            a4.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorDescription.setText("");
            a4.setStyle("");
        }

        // Validation Ressource (URL)
        if (ressource.isEmpty()) {
            errorRessource.setText("Champ obligatoire");
            a5.setStyle("-fx-border-color: red;");
            isValid = false;
        } else if (!ressource.startsWith("http://") && !ressource.startsWith("https://")) {
            errorRessource.setText("URL invalide");
            a5.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorRessource.setText("");
            a5.setStyle("");
        }

        if (!isValid) return;

        // Création et ajout du projet
        Projet p = new Projet(nom, statut, priorite, description, ressource);
        try {
            projetService.add(p);
            showAlert("Succès", "Projet ajouté !");
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Échec de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    void afficher(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AfficherProjet.fxml")));
            if (a1 != null && a1.getScene() != null) {
                a1.getScene().setRoot(root);
            } else {
                System.out.println("Scene or TextField is null, cannot set root.");
            }
        } catch (IOException e) {
            System.out.println("Error loading AfficherProjet.fxml: " + e.getMessage());
        }
    }

    @FXML
    void afficherStats(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/StatistiquesProjet.fxml")));
            if (a1 != null && a1.getScene() != null) {
                a1.getScene().setRoot(root);
            } else {
                System.out.println("Scene or TextField is null, cannot set root.");
            }
        } catch (IOException e) {
            System.out.println("Error loading StatistiquesProjet.fxml: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        if (a1 != null) a1.clear();
        if (a2 != null) a2.setValue(null);
        if (a3 != null) a3.setValue(null);
        if (a4 != null) a4.clear();
        if (a5 != null) a5.clear();

        errorNom.setText(""); errorStatut.setText(""); errorPriorite.setText("");
        errorDescription.setText(""); errorRessource.setText("");
        a1.setStyle(""); a4.setStyle(""); a5.setStyle("");
    }

    // Méthodes vocales inchangées (vous pouvez les garder telles quelles)

    @FXML
    void handleVoiceInputNom() { handleVoiceInput(a1, "Nom"); }
    @FXML
    void handleVoiceInputStatut() { handleVoiceInputForCombo(a2, "Statut"); }
    @FXML
    void handleVoiceInputPriorite() { handleVoiceInputForCombo(a3, "Priorité"); }
    @FXML
    void handleVoiceInputDescription() { handleVoiceInput(a4, "Description"); }
    @FXML
    void handleVoiceInputRessource() { handleVoiceInput(a5, "Ressource"); }

    private void handleVoiceInput(TextField field, String fieldName) {
        try {
            String pythonScriptPath = "C:\\Users\\Mega-Pc\\Desktop\\pi-int\\pi\\Projet\\python\\speechToText.py";
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, fieldName);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            p.waitFor();
            String transcribedText = output.toString().trim();
            if (!transcribedText.isEmpty() && field != null) {
                field.setText(transcribedText);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'entrée vocale pour " + fieldName);
        }
    }

    private void handleVoiceInputForCombo(ComboBox<String> combo, String fieldName) {
        try {
            String pythonScriptPath = "C:\\Users\\Mega-Pc\\Desktop\\pi-int\\pi\\Projet\\python\\speechToText.py";
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, fieldName);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            p.waitFor();
            String transcribedText = output.toString().trim().toLowerCase();
            if (!transcribedText.isEmpty() && combo != null) {
                for (String option : combo.getItems()) {
                    if (option.toLowerCase().contains(transcribedText)) {
                        combo.setValue(option);
                        return;
                    }
                }
                showAlert("Avertissement", "Aucune option trouvée pour '" + transcribedText + "'");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'entrée vocale pour " + fieldName);
        }
    }
}