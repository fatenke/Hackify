package gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Technologie;
import services.TechnologieService;
import services.TechnologieService.CompatibilityStat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AjouterTechnologie {

    private final TechnologieService technologieService = new TechnologieService();

    @FXML private TextField t1;
    @FXML private ComboBox<String> t2;
    @FXML private ComboBox<String> t3;
    @FXML private ComboBox<String> t4;
    @FXML private TextField t5;
    @FXML private TabPane tabPane;

    @FXML private TextField searchFieldTech;
    @FXML private Button searchButtonTech;
    private Technologie selectedTechnologie = null;

    @FXML private GridPane gridTech;
    @FXML private AnchorPane chartContainer;
    @FXML private TableView<CompatibilityStat> statsTable;
    @FXML private TableColumn<CompatibilityStat, String> compatibilityColumn;
    @FXML private TableColumn<CompatibilityStat, Integer> countColumn;

    @FXML private Label errorNom, errorType, errorComplexite, errorCompatibilite, errorDoc;

    @FXML
    public void initialize() {
        t2.setItems(FXCollections.observableArrayList("dev","business","finance","sécurité","réseau","autre"));
        t3.setItems(FXCollections.observableArrayList("Haute","Moyenne","Faible"));
        t4.setItems(FXCollections.observableArrayList("Windows","Linux","macOS"));

        searchButtonTech.setOnAction(e -> filterTechnologies(searchFieldTech.getText()));
        searchFieldTech.textProperty().addListener((o,old,n) -> filterTechnologies(n));

        compatibilityColumn.setCellValueFactory(new PropertyValueFactory<>("compatibility"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        Platform.runLater(() -> {
            refreshTechnologies();
            refreshStats();
        });
    }

    @FXML
    private void ajouterAction() {
        boolean valid = true;
        errorNom.setText("");
        errorType.setText("");
        errorComplexite.setText("");
        errorCompatibilite.setText("");
        errorDoc.setText("");

        if (t1.getText().trim().isEmpty()) {
            errorNom.setText("Nom requis");
            valid = false;
        }
        if (t2.getValue() == null) {
            errorType.setText("Type requis");
            valid = false;
        }
        if (t3.getValue() == null) {
            errorComplexite.setText("Complexité requise");
            valid = false;
        }
        if (t4.getValue() == null) {
            errorCompatibilite.setText("Compatibilité requise");
            valid = false;
        }
        if (t5.getText().trim().isEmpty()) {
            errorDoc.setText("Documentaire requis");
            valid = false;
        }

        if (!valid) return;

        if (selectedTechnologie == null) {
            // New technologie
            Technologie tech = new Technologie(
                    t1.getText().trim(),
                    t2.getValue(),
                    t3.getValue(),
                    t5.getText().trim(),
                    t4.getValue()
            );
            technologieService.add(tech);
        } else {
            // Update existing
            selectedTechnologie.setNom_tech(t1.getText().trim());
            selectedTechnologie.setType_tech(t2.getValue());
            selectedTechnologie.setComplexite(t3.getValue());
            selectedTechnologie.setCompatibilite(t4.getValue());
            selectedTechnologie.setDocumentaire(t5.getText().trim());

            technologieService.update(selectedTechnologie);
            selectedTechnologie = null;
        }

        refreshTechnologies();
        refreshStats();

        t1.clear();
        t2.getSelectionModel().clearSelection();
        t3.getSelectionModel().clearSelection();
        t4.getSelectionModel().clearSelection();
        t5.clear();
    }



    @FXML
    void refreshTechnologies() {
        List<Technologie> list = technologieService.getAll();
        gridTech.getChildren().clear();
        int c = 0, r = 0;
        for (Technologie t : list) {
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-background-radius: 10px; -fx-border-color: #ddd; -fx-border-radius: 10px;");

            Label nomLabel = new Label("Nom: " + t.getNom_tech());
            Label typeLabel = new Label("Type: " + t.getType_tech());
            Label compLabel = new Label("Complexité: " + t.getComplexite());
            Label compatLabel = new Label("Compatibilité: " + t.getCompatibilite());

            Button editBtn = new Button("Modifier");
            editBtn.setOnAction(e -> {
                populateForm(t);
                tabPane.getSelectionModel().select(0);  // switch to the "Ajouter" tab
            });

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.setOnAction(e -> {
                technologieService.delete(t.getId_tech());
                refreshTechnologies();
                refreshStats();
            });

            HBox buttons = new HBox(10, editBtn, deleteBtn);
            card.getChildren().addAll(nomLabel, typeLabel, compLabel, compatLabel, buttons);

            gridTech.add(card, c, r);
            if (++c == 3) { c = 0; r++; }
        }
    }

    private void populateForm(Technologie tech) {
        selectedTechnologie = tech;
        t1.setText(tech.getNom_tech());
        t2.setValue(tech.getType_tech());
        t3.setValue(tech.getComplexite());
        t4.setValue(tech.getCompatibilite());
        t5.setText(tech.getDocumentaire());
    }



    private void filterTechnologies(String txt){
        List<Technologie> f=technologieService.getAll().stream()
                .filter(t->t.getNom_tech().toLowerCase().contains(txt.toLowerCase()))
                .collect(Collectors.toList());
        gridTech.getChildren().clear();
        int c=0,r=0;
        for(Technologie t: f){
            VBox card=new VBox(10);
            card.getChildren().add(new Label(t.getNom_tech()));
            gridTech.add(card,c,r);
            if(++c==3){c=0;r++;}
        }
    }

    @FXML
    private void refreshStats(){
        List<CompatibilityStat> stats=technologieService.getCompatibilityStats();
        statsTable.setItems(FXCollections.observableArrayList(stats));
        chartContainer.getChildren().clear();
        CategoryAxis x=new CategoryAxis();
        NumberAxis y=new NumberAxis();
        BarChart<String,Number> chart=new BarChart<>(x,y);
        XYChart.Series<String,Number> s=new XYChart.Series<>();
        for(CompatibilityStat st:stats){
            s.getData().add(new XYChart.Data<>(st.getCompatibility(),st.getCount()));
        }
        chart.getData().add(s);
        chartContainer.getChildren().add(chart);
    }
}