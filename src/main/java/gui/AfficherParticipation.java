package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class AfficherParticipation {
    @FXML
    public void start(Stage primaryStage) {
        // Titre de la fenêtre
        primaryStage.setTitle("Inscription Hackathon");

        // Création de la VBox principale pour le layout
        VBox vbox = new VBox(20);
        vbox.setPadding(new javafx.geometry.Insets(20));

        // Titre principal
        Label titreLabel = new Label("Inscription au Hackathon");
        titreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Boutons pour l'inscription individuelle et par équipe
        Button btnIndividuel = new Button("Inscription Individuelle");
        Button btnEquipe = new Button("Inscription en Équipe");

        // Ajouter les boutons à la VBox
        vbox.getChildren().addAll(titreLabel, btnIndividuel, btnEquipe);

        // Pane pour afficher les formulaires dynamiquement
        StackPane formulairePane = new StackPane();
        vbox.getChildren().add(formulairePane);

        // Bouton pour l'inscription individuelle
        btnIndividuel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Vider le formulaire actuel
                formulairePane.getChildren().clear();

                // Créer le formulaire pour l'inscription individuelle
                VBox formulaireIndividuel = new VBox(10);
                Label nomLabel = new Label("Nom:");
                TextField nomField = new TextField();
                Label emailLabel = new Label("Email:");
                TextField emailField = new TextField();

                Button submitButton = new Button("S'inscrire");

                // Ajouter des événements pour soumettre les informations
                submitButton.setOnAction(e -> {
                    String nom = nomField.getText();
                    String email = emailField.getText();
                    // Logique pour traiter l'inscription (par exemple, appel à un service)
                    System.out.println("Inscription individuelle: " + nom + " - " + email);
                });

                formulaireIndividuel.getChildren().addAll(nomLabel, nomField, emailLabel, emailField, submitButton);
                formulairePane.getChildren().add(formulaireIndividuel);
            }
        });

        // Bouton pour l'inscription en équipe
        btnEquipe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Vider le formulaire actuel
                formulairePane.getChildren().clear();

                // Créer le formulaire pour l'inscription en équipe
                VBox formulaireEquipe = new VBox(10);
                Label nomEquipeLabel = new Label("Nom de l'équipe:");
                TextField nomEquipeField = new TextField();
                Label nombreMembresLabel = new Label("Nombre de membres:");
                TextField nombreMembresField = new TextField();

                Button submitButtonEquipe = new Button("S'inscrire");

                // Ajouter des événements pour soumettre les informations
                submitButtonEquipe.setOnAction(e -> {
                    String nomEquipe = nomEquipeField.getText();
                    String nombreMembres = nombreMembresField.getText();
                    // Logique pour traiter l'inscription de l'équipe (par exemple, appel à un service)
                    System.out.println("Inscription équipe: " + nomEquipe + " - Membres: " + nombreMembres);
                });

                formulaireEquipe.getChildren().addAll(nomEquipeLabel, nomEquipeField, nombreMembresLabel, nombreMembresField, submitButtonEquipe);
                formulairePane.getChildren().add(formulaireEquipe);
            }
        });

        // Scène et affichage
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
