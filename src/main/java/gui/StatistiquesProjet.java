package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import models.Projet;
import services.ProjetService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatistiquesProjet {

    @FXML
    private PieChart pieChartStatut;
    
    @FXML
    private Label labelTotal;

    private final ProjetService projetService = new ProjetService();

    @FXML
    void initialize() {
        chargerStatistiques();
    }

    /**
     * Charge les statistiques des projets par statut
     */
    private void chargerStatistiques() {
        // Récupérer tous les projets
        List<Projet> projets = projetService.getAll();
        
        // Compter le nombre de projets par statut
        Map<String, Integer> statsByStatut = new HashMap<>();
        
        for (Projet projet : projets) {
            String statut = projet.getStatut();
            if (statut != null && !statut.isEmpty()) {
                statsByStatut.put(statut, statsByStatut.getOrDefault(statut, 0) + 1);
            }
        }
        
        // Créer les données pour le graphique
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Integer> entry : statsByStatut.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }
        
        // Mettre à jour le graphique
        pieChartStatut.setData(pieChartData);
        pieChartStatut.setTitle("Répartition des projets par statut");
        
        // Personnaliser l'apparence du graphique
        pieChartStatut.setLabelsVisible(true);
        pieChartStatut.setLegendVisible(true);
        
        // Ajouter des couleurs personnalisées pour chaque statut
        int colorIndex = 0;
        Color[] colors = {
            Color.web("#3498DB"), // Bleu
            Color.web("#E74C3C"), // Rouge
            Color.web("#2ECC71"), // Vert
            Color.web("#F39C12"), // Orange
            Color.web("#9B59B6")  // Violet
        };
        
        for (PieChart.Data data : pieChartData) {
            String style = String.format("-fx-pie-color: %s;", 
                    toHexString(colors[colorIndex % colors.length]));
            data.getNode().setStyle(style);
            colorIndex++;
        }
        
        // Mettre à jour le label avec le total
        labelTotal.setText("Total des projets: " + projets.size());
    }
    
    /**
     * Convertit une couleur JavaFX en chaîne hexadécimale
     */
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Action pour retourner à la page d'ajout des projets
     */
    @FXML
    void retourAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AjoutProjet.fxml")));
            pieChartStatut.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de AjoutProjet.fxml: " + e.getMessage());
        }
    }
}
