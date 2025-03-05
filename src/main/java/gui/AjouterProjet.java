package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import models.Projet;
import services.ProjetService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class AjouterProjet {
    private final ProjetService projetService = new ProjetService();

    @FXML
    private TextField a1; // For the "nom" field

    @FXML
    private ComboBox<String> a2; // For the "statut" ComboBox

    @FXML
    private ComboBox<String> a3; // For the "priorité" ComboBox

    @FXML
    private TextField a4; // For the "description" field

    @FXML
    private TextField a5; // For the "ressource" field

    @FXML
    private Button listenNom;
    @FXML
    private Button listenStatut;
    @FXML
    private Button listenPriorite;
    @FXML
    private Button listenDescription;
    @FXML
    private Button listenRessource;

   // @FXML
   // void initialize() {
        // Initialize ComboBox options to match the FXML items (adjust as needed)
     //   if (a2 != null) {
       //     a2.getItems().addAll("En cours", "En pause", "Terminé"); // Match FXML options
        //}
        //if (a3 != null) {
          //  a3.getItems().addAll("Haute", "Moyenne", "Faible"); // Match FXML options
        // }
    // }

    @FXML
    void ajouterAction(ActionEvent event) {
        String nom = a1.getText();
        String statut = a2.getValue();
        String priorite = a3.getValue();
        String description = a4.getText();
        String ressource = a5.getText();

        // Input validation
        if (nom == null || nom.trim().isEmpty()) {
            showAlert("Erreur", "Le nom du projet ne peut pas être vide.");
            return;
        }
        if (statut == null) {
            showAlert("Erreur", "Veuillez sélectionner un statut.");
            return;
        }
        if (priorite == null) {
            showAlert("Erreur", "Veuillez sélectionner une priorité.");
            return;
        }
        if (ressource == null || ressource.trim().isEmpty()) {
            showAlert("Erreur", "La ressource ne peut pas être vide.");
            return;
        }

        Projet p = new Projet(nom, statut, priorite, description, ressource);

        try {
            projetService.add(p);
            showAlert("Succès", "Projet ajouté avec succès!");
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de l'ajout : " + e.getMessage());
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        if (a1 != null) a1.clear();
        if (a2 != null) a2.setValue(null);
        if (a3 != null) a3.setValue(null);
        if (a4 != null) a4.clear();
        if (a5 != null) a5.clear();
    }

    @FXML
    void handleVoiceInputNom() {
        handleVoiceInput(a1, "Nom");
    }

    @FXML
    void handleVoiceInputStatut() {
        handleVoiceInputForCombo(a2, "Statut");
    }

    @FXML
    void handleVoiceInputPriorite() {
        handleVoiceInputForCombo(a3, "Priorité");
    }

    @FXML
    void handleVoiceInputDescription() {
        handleVoiceInput(a4, "Description");
    }

    @FXML
    void handleVoiceInputRessource() {
        handleVoiceInput(a5, "Ressource");
    }

    private void handleVoiceInput(TextField field, String fieldName) {
        try {
            // Update the path to your Python STT script (replace with your actual path)
            String pythonScriptPath = "C:\\Users\\Mega-Pc\\Desktop\\pi\\Projet\\python\\speechToText.py"; // Adjust this path
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, fieldName);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            p.waitFor();

            String transcribedText = output.toString().trim();
            if (!transcribedText.isEmpty()) {
                if (field != null) {
                    field.setText(transcribedText);
                } else {
                    System.out.println("TextField is null for " + fieldName);
                }
            } else {
                System.out.println("No speech detected or transcription failed for " + fieldName);
                showAlert("Erreur", "Aucun son détecté ou échec de la transcription pour " + fieldName);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error with voice input for " + fieldName + ": " + e.getMessage());
            showAlert("Erreur", "Une erreur est survenue lors de l'entrée vocale pour " + fieldName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleVoiceInputForCombo(ComboBox<String> combo, String fieldName) {
        try {
            String pythonScriptPath = "C:\\Users\\Mega-Pc\\Desktop\\pi\\Projet\\python\\speechToText.py"; // Adjust this path
            ProcessBuilder pb = new ProcessBuilder("python", pythonScriptPath, fieldName);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            p.waitFor();

            String transcribedText = output.toString().trim().toLowerCase();
            if (!transcribedText.isEmpty()) {
                if (combo != null) {
                    for (String option : combo.getItems()) {
                        if (option.toLowerCase().contains(transcribedText)) {
                            combo.setValue(option);
                            return;
                        }
                    }
                    System.out.println("No matching option found for " + transcribedText + " in " + fieldName);
                    showAlert("Avertissement", "Aucune option correspondante trouvée pour '" + transcribedText + "' dans " + fieldName);
                } else {
                    System.out.println("ComboBox is null for " + fieldName);
                }
            } else {
                System.out.println("No speech detected or transcription failed for " + fieldName);
                showAlert("Erreur", "Aucun son détecté ou échec de la transcription pour " + fieldName);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error with voice input for " + fieldName + ": " + e.getMessage());
            showAlert("Erreur", "Une erreur est survenue lors de l'entrée vocale pour " + fieldName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}