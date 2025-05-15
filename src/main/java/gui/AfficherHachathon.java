package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Hackathon;
import models.User;
import services.HackathonService;
import services.ParticipationService;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import util.SessionManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AfficherHachathon {

    private final HackathonService hackathonService = new HackathonService();
    private final ParticipationService participationService = new ParticipationService();

    @FXML
    private GridPane gp_hackathon;

    @FXML
    void initialize() {
        loadHackathons();
    }

    @FXML
    private void handleAfficherTousHackathons(ActionEvent event) {
        gp_hackathon.getChildren().clear();
        loadHackathons();
    }

    @FXML
    private void handleAfficherMesHackathons(ActionEvent event) {
        String sessionId = SessionManager.getLastSessionId(); // récupère la dernière session créée
        if (sessionId != null) {
            User currentUser = SessionManager.getSession(sessionId);
            if (currentUser != null) {
                int idOrganisateur = currentUser.getId(); // supposé que User a getId()
                gp_hackathon.getChildren().clear();
                afficherHackathonsParOrganisateur(idOrganisateur);
            } else {
                System.out.println("Utilisateur non trouvé pour la session.");
            }
        } else {
            System.out.println("Aucune session active.");
        }
    }

    public void loadHackathons() {
        afficherHackathons(hackathonService.getAll());
    }

    public void afficherHackathonsParOrganisateur(int idOrganisateur) {
        afficherHackathons(hackathonService.getHackathonByIdOrganisateur(idOrganisateur));
    }

    private void afficherHackathons(List<Hackathon> hackathons) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        int columns = 3;
        int row = 0, col = 0;

        for (Hackathon h : hackathons) {
            StackPane stack = new StackPane();
            stack.getStyleClass().add("hackathon-card");
            stack.setPadding(new Insets(0));
            Rectangle frontFace = new Rectangle(330, 230);
            frontFace.setFill(Color.WHITE);
            frontFace.setStroke(Color.LIGHTGRAY);
            frontFace.setStrokeWidth(2);
            frontFace.setArcWidth(60);
            frontFace.setArcHeight(30);
            StackPane.setAlignment(frontFace, Pos.TOP_RIGHT);
            stack.getStyleClass().add("frontFace");

            VBox textContainer = new VBox(5);
            textContainer.setAlignment(Pos.CENTER);
            textContainer.setPadding(new Insets(10));
            textContainer.getStyleClass().add("textContainer");

            Label nom = new Label(h.getNom_hackathon());
            nom.setFont(new Font("Arial", 16));
            nom.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

            Label date1 = new Label("📅 " + h.getDate_debut().format(formatter));
            date1.setStyle("-fx-text-fill: #555;");
            Label date2 = new Label("📅 " + h.getDate_fin().format(formatter));
            date2.setStyle("-fx-text-fill: #555;");
            Label lieu = new Label("📍 " + h.getLieu());
            lieu.setStyle("-fx-text-fill: #777;");

            HBox hbox = new HBox(10);
            hbox.setAlignment(Pos.CENTER);


            if (participationService.getNebrParticipantPerHackathon(h.getId_hackathon()) < h.getMax_participants()) {
                Button participateButton = new Button("Details");
                participateButton.getStyleClass().add("btn-action");
                participateButton.setOnAction(event -> participerHackathon(h));
                textContainer.getChildren().addAll(nom, date1, date2, lieu, hbox, participateButton);
            } else {
                textContainer.getChildren().addAll(nom, date1, date2, lieu, hbox);
                participationService.refuserParticipationsEnAttente(h.getId_hackathon());
            }

            stack.getChildren().addAll(frontFace, textContainer);
            StackPane.setMargin(frontFace, new Insets(-10, -12, 0, 0));

            gp_hackathon.add(stack, col, row);
            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }
    }

    private void participerHackathon(Hackathon hackathon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HackathonDetails.fxml"));
            Parent newContent = loader.load();
            HackathonDetails controller = loader.getController();
            controller.setHackathon(hackathon);
            Stage stage = (Stage) gp_hackathon.getScene().getWindow();
            stage.getScene().setRoot(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
