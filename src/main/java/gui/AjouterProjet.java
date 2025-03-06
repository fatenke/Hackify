package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import models.Projet;
import services.ProjetService;

import java.io.*;
import java.util.Objects;

public class AjouterProjet {
    private final ProjetService projetService = new ProjetService();

    // Path to the Python script for voice input (relative to project root)
    private static final String PYTHON_SCRIPT_PATH = "python/speechToText.py"; // Adjust this if necessary

    // Directory for saving uploaded files (user's home directory + app-specific folder)
    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/hackify/uploads/";

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

    @FXML
    private Button uploadFileButton; // Bouton pour uploader un fichier

    @FXML
    private TextField uploadedFileName; // Pour afficher le nom du fichier uploadé

    @FXML
    void initialize() {
        // Initialize ComboBox options to match the FXML items
        if (a2 != null) {
            a2.getItems().addAll("En cours", "En pause", "Terminé");
        }
        if (a3 != null) {
            a3.getItems().addAll("Haute", "Moyenne", "Faible");
        }

        // Configurer le bouton pour uploader un fichier
        if (uploadFileButton != null) {
            uploadFileButton.setOnAction(event -> uploadFile());
        }

        // Add a tooltip to uploadedFileName to show the full file name on hover
        if (uploadedFileName != null) {
            Tooltip tooltip = new Tooltip();
            uploadedFileName.textProperty().addListener((observable, oldValue, newValue) -> {
                tooltip.setText(newValue);
                uploadedFileName.setTooltip(tooltip);
            });
        }
    }

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
            clearUploadedFile(); // Clear the uploaded file field after successful addition
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

    private void clearUploadedFile() {
        if (uploadedFileName != null) {
            uploadedFileName.clear();
        }
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
            // Use the relative path to the Python script
            ProcessBuilder pb = new ProcessBuilder("python", PYTHON_SCRIPT_PATH, fieldName);
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
            // Use the relative path to the Python script
            ProcessBuilder pb = new ProcessBuilder("python", PYTHON_SCRIPT_PATH, fieldName);
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

    @FXML
    private void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier");
        // Définir les types de fichiers acceptés (par exemple, PDF, images, documents)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*"),
                new FileChooser.ExtensionFilter("Documents PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Documents texte", "*.txt")
        );

        // Ouvrir la fenêtre de sélection de fichier
        File selectedFile = fileChooser.showOpenDialog(uploadFileButton.getScene().getWindow());
        if (selectedFile != null) {
            // Limiter la taille du fichier à 100 Mo (100 * 1024 * 1024 bytes = 104,857,600 bytes)
            long maxSize = 100L * 1024 * 1024; // 100 Mo
            if (selectedFile.length() > maxSize) {
                showAlert("Erreur", "Le fichier est trop volumineux (max 100 Mo).");
                clearUploadedFile();
                return;
            }

            try {
                // Chemin de destination pour sauvegarder le fichier (dans le répertoire utilisateur)
                String destinationPath = UPLOAD_DIR + selectedFile.getName();

                // Créer le répertoire s'il n'existe pas
                File destDir = new File(UPLOAD_DIR);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                // Copier le fichier sélectionné vers le répertoire de destination
                copyFile(selectedFile, new File(destinationPath));

                // Afficher le nom du fichier dans le TextField
                uploadedFileName.setText(selectedFile.getName());
                showAlert("Succès", "Fichier uploadé avec succès : " + selectedFile.getName());
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de l'upload du fichier : " + e.getMessage());
                clearUploadedFile(); // Clear the field if the upload fails
            }
        } else {
            showAlert("Avertissement", "Aucun fichier sélectionné.");
            clearUploadedFile();
        }
    }

    private void copyFile(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}