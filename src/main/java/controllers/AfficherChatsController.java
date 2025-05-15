package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Chat;
import models.Message;
import models.Poll;
import models.PollOption;
import models.Reaction;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;
import services.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AfficherChatsController {

    @FXML private Label chatTitle;
    @FXML private ListView<String> chatsListView;
    @FXML private ListView<Object> messagesListView;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private Button returnButton;
    @FXML private TextField searchField;
    @FXML private ListView<Message> searchResultsListView;
    @FXML private Label noResultsLabel;
    @FXML private HBox suggestionBox;
    @FXML private TextField pollQuestionField;
    @FXML private TextField pollOption1Field;
    @FXML private TextField pollOption2Field;
    @FXML private Button createPollButton;
    @FXML private Button cancelPollButton;
    @FXML private VBox pollCreationPopout;
    @FXML private Button pollIconButton;

    private GeminiService geminiService;
    private ChatService chatService;
    private MessageService messageService;
    private PollService pollService;
    private PollOptionService pollOptionService;
    private PollVoteService voteService;
    private ReactionService reactionService;
    private int currentUserId = 6;
    private int currentCommunityId;
    private Chat selectedChat;

    private static final String[] SUGGESTIONS = {
            "Comment rejoindre un hackathon ?",
            "Où trouver des ressources ?",
            "Où puis-je trouver mon portefeuille ?"
    };

    public void setCurrentCommunityId(int id) {
        this.currentCommunityId = id;
        loadChats();
    }

    @FXML
    public void initialize() {
        chatService = new ChatService();
        geminiService = new GeminiService("AIzaSyAdtU0BkTPvbpbKhK1J6AGNaSwaywhByZc");
        messageService = new MessageService(chatService, geminiService);
        pollService = new PollService();
        pollOptionService = new PollOptionService();
        voteService = new PollVoteService();
        reactionService = new ReactionService();

        messagesListView.setCellFactory(lv -> new MessageCell());

        searchResultsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String chatName = "Chat inconnu";
                    Chat chat = chatService.getChatById(item.getChatId());
                    if (chat != null) {
                        chatName = chat.getNom();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.FRENCH);
                    String timeFormatted = sdf.format(item.getPostTime());
                    TextFlow messageFlow = new TextFlow();
                    String content = item.getContenu();
                    String searchTerm = searchField.getText().trim();
                    if (!searchTerm.isEmpty()) {
                        String lowerContent = content.toLowerCase();
                        String lowerTerm = searchTerm.toLowerCase();
                        int index = lowerContent.indexOf(lowerTerm);
                        if (index >= 0) {
                            Text before = new Text(content.substring(0, index));
                            Text highlighted = new Text(content.substring(index, index + searchTerm.length()));
                            Text after = new Text(content.substring(index + searchTerm.length()));
                            highlighted.setFill(Color.BLACK);
                            highlighted.setStyle("-fx-background-color: yellow; -fx-font-weight: bold;");
                            messageFlow.getChildren().addAll(before, highlighted, after);
                        } else {
                            messageFlow.getChildren().add(new Text(content));
                        }
                    } else {
                        messageFlow.getChildren().add(new Text(content));
                    }
                    VBox vbox = new VBox(5);
                    Label chatNameLabel = new Label(chatName);
                    chatNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: gray;");
                    Label timeLabel = new Label(timeFormatted + " - Utilisateur: " + item.getPostedBy());
                    timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
                    vbox.getChildren().addAll(chatNameLabel, messageFlow, timeLabel);
                    setGraphic(vbox);
                }
            }
        });

        searchResultsListView.setOnMouseClicked(event -> {
            Message selectedMessage = searchResultsListView.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                if (selectedChat == null || selectedMessage.getChatId() != selectedChat.getId()) {
                    Chat targetChat = chatService.getChatById(selectedMessage.getChatId());
                    if (targetChat != null) {
                        selectedChat = targetChat;
                        chatTitle.setText(targetChat.getNom());
                        chatsListView.getSelectionModel().select(targetChat.getNom());
                        loadMessagesForChat(targetChat.getId());
                    }
                }
                PauseTransition pause = new PauseTransition(Duration.millis(150));
                pause.setOnFinished(e -> {
                    int index = getMessageIndex(selectedMessage);
                    if (index >= 0) {
                        messagesListView.scrollTo(index);
                        messagesListView.getSelectionModel().select(index);
                    }
                });
                pause.play();
                searchResultsListView.setVisible(false);
                searchResultsListView.setManaged(false);
                noResultsLabel.setVisible(false);
                noResultsLabel.setManaged(false);
            }
        });

        // Bind pollIconButton to show poll popout
        pollIconButton.setOnAction(e -> handleShowPollPopout());

        loadActivePolls();
    }

    @FXML
    private void handleSearch() {
        String term = searchField.getText().trim();
        if (term.isEmpty()) {
            searchResultsListView.setVisible(false);
            searchResultsListView.setManaged(false);
            noResultsLabel.setVisible(false);
            noResultsLabel.setManaged(false);
            return;
        }
        try {
            List<Message> results = messageService.searchMessages(term);
            if (results == null || results.isEmpty()) {
                searchResultsListView.setVisible(false);
                searchResultsListView.setManaged(false);
                noResultsLabel.setVisible(true);
                noResultsLabel.setManaged(true);
            } else {
                searchResultsListView.setItems(FXCollections.observableArrayList(results));
                searchResultsListView.setVisible(true);
                searchResultsListView.setManaged(true);
                noResultsLabel.setVisible(false);
                noResultsLabel.setManaged(false);
            }
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de la recherche des messages : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelSearch() {
        searchField.clear();
        searchResultsListView.setVisible(false);
        searchResultsListView.setManaged(false);
        noResultsLabel.setVisible(false);
        noResultsLabel.setManaged(false);
    }

    private int getMessageIndex(Message selectedMessage) {
        for (int i = 0; i < messagesListView.getItems().size(); i++) {
            if (messagesListView.getItems().get(i) instanceof Message &&
                    ((Message) messagesListView.getItems().get(i)).getId() == selectedMessage.getId()) {
                return i;
            }
        }
        return -1;
    }

    private void loadChats() {
        chatsListView.getItems().clear();
        List<Chat> communityChats = chatService.getChatsByCommunityId(currentCommunityId);
        if (communityChats == null || communityChats.isEmpty()) {
            showAlert("Information", "Aucun chat trouvé pour cette communauté.");
            return;
        }
        for (Chat c : communityChats) {
            chatsListView.getItems().add(c.getNom());
        }
        chatsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                for (Chat c : communityChats) {
                    if (c.getNom().equals(newVal)) {
                        selectedChat = c;
                        chatTitle.setText(c.getNom());
                        loadMessagesForChat(c.getId());
                        loadActivePolls();
                        break;
                    }
                }
            }
        });
        if (!chatsListView.getItems().isEmpty()) {
            chatsListView.getSelectionModel().select(0);
        }
    }

    private void loadMessagesForChat(int chatId) {
        try {
            List<Message> chatMessages = messageService.getMessagesByChatId(chatId);
            if (chatMessages == null) {
                chatMessages = new ArrayList<>();
            }
            List<Object> items = new ArrayList<>(chatMessages);
            messagesListView.getItems().setAll(items);
            displaySuggestions();
            loadActivePolls();
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors du chargement des messages : " + e.getMessage());
        }
    }

    private void handleSuggestionClick(String suggestionText) {
        messageField.setText(suggestionText);
        handleSendMessage();
    }

    @FXML
    private void handleSendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty() || selectedChat == null) {
            showAlert("Erreur", "Le message ne peut pas être vide ou aucun chat sélectionné.");
            return;
        }
        try {
            Message newMessage = new Message(
                    selectedChat.getId(),
                    content,
                    new Timestamp(System.currentTimeMillis()),
                    currentUserId
            );
            messageService.add(newMessage);
            messageField.clear();
            loadMessagesForChat(selectedChat.getId());
            displaySuggestions();
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de l'envoi du message : " + e.getMessage());
        }
    }

    @FXML
    private void handleReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainLayout.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du retour : " + e.getMessage());
        }
    }

    private void displaySuggestions() {
        suggestionBox.getChildren().clear();
        if (selectedChat == null) {
            return;
        }
        if (!"BOT_SUPPORT".equals(selectedChat.getType())) {
            return;
        }
        for (String suggestion : SUGGESTIONS) {
            Button suggestionBtn = new Button(suggestion);
            suggestionBtn.setStyle("-fx-background-color: #4A148C; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 10; -fx-cursor: hand;");
            suggestionBtn.setOnAction(e -> handleSuggestionClick(suggestion));
            suggestionBox.getChildren().add(suggestionBtn);
        }
    }

    @FXML
    private void handleShowPollPopout() {
        animatePollPopout();
    }

    private void animatePollPopout() {
        pollCreationPopout.setOpacity(0);
        pollCreationPopout.setVisible(true);
        pollCreationPopout.setManaged(true);
        FadeTransition ft = new FadeTransition(Duration.millis(300), pollCreationPopout);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    @FXML
    private void handleCreatePoll() {
        String question = pollQuestionField.getText().trim();
        String option1 = pollOption1Field.getText().trim();
        String option2 = pollOption2Field.getText().trim();

        if (question.isEmpty() || option1.isEmpty() || option2.isEmpty() || selectedChat == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs du sondage.");
            return;
        }
        if (option1.equals(option2)) {
            showAlert("Erreur", "Les options du sondage doivent être différentes.");
            return;
        }

        try {
            List<PollOption> options = new ArrayList<>();
            options.add(new PollOption(0, option1));
            options.add(new PollOption(0, option2));
            Poll poll = new Poll(selectedChat.getId(), question, options);
            pollService.add(poll);

            if (poll.getId() == 0) {
                showAlert("Erreur", "Échec de la création du sondage.");
                return;
            }

            pollQuestionField.clear();
            pollOption1Field.clear();
            pollOption2Field.clear();
            handleCancelPoll();
            loadMessagesForChat(selectedChat.getId());
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors de la création du sondage : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelPoll() {
        pollCreationPopout.setVisible(false);
        pollCreationPopout.setManaged(false);
        pollQuestionField.clear();
        pollOption1Field.clear();
        pollOption2Field.clear();
    }

    private void handleVote(int pollId, ToggleGroup toggleGroup) {
        if (selectedChat == null) {
            showAlert("Erreur", "Aucun chat sélectionné.");
            return;
        }

        RadioButton selectedOption = (RadioButton) toggleGroup.getSelectedToggle();
        if (selectedOption == null) {
            showAlert("Erreur", "Veuillez sélectionner une option pour voter.");
            return;
        }

        try {
            int optionId = (int) selectedOption.getUserData();
            Poll poll = pollService.getById(pollId);
            if (poll == null) {
                showAlert("Erreur", "Sondage non trouvé.");
                return;
            }
            if (poll.isClosed()) {
                showAlert("Information", "Ce sondage est fermé et n'accepte plus de votes.");
                return;
            }
            if (voteService.hasUserVoted(pollId, currentUserId)) {
                showAlert("Information", "Vous avez déjà voté dans ce sondage.");
                return;
            }

            voteService.recordVote(pollId, optionId, currentUserId);
            loadActivePolls();
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors du vote : " + e.getMessage());
        }
    }

    private void handleClosePoll(int pollId) {
        if (selectedChat == null) {
            showAlert("Erreur", "Aucun chat sélectionné.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la fermeture");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir fermer ce sondage ?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                pollService.closePoll(pollId);
                showAlert("Succès", "Sondage fermé avec succès.");
                loadActivePolls();
            } catch (RuntimeException e) {
                showAlert("Erreur", "Erreur lors de la fermeture du sondage : " + e.getMessage());
            }
        }
    }

    private void handleDeletePoll(int pollId) {
        if (selectedChat == null) {
            showAlert("Erreur", "Aucun chat sélectionné.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce sondage ?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Poll poll = pollService.getById(pollId);
                if (poll != null) {
                    pollService.delete(poll);
                    showAlert("Succès", "Sondage supprimé avec succès.");
                } else {
                    showAlert("Erreur", "Sondage non trouvé.");
                }
                loadActivePolls();
            } catch (RuntimeException e) {
                showAlert("Erreur", "Erreur lors de la suppression du sondage : " + e.getMessage());
            }
        }
    }

    private void loadActivePolls() {
        if (selectedChat == null) {
            removeAllPollsFromChat();
            return;
        }

        try {
            List<Poll> polls = pollService.getPollsByChatId(selectedChat.getId());
            List<VBox> pollNodes = new ArrayList<>();

            for (Poll poll : polls) {
                if (!poll.isClosed()) {
                    VBox pollNode = createPollNode(poll);
                    if (pollNode != null) {
                        pollNodes.add(pollNode);
                    }
                }
            }

            if (!pollNodes.isEmpty()) {
                List<Object> currentItems = new ArrayList<>(messagesListView.getItems());
                currentItems.removeIf(item -> item instanceof VBox);
                currentItems.addAll(pollNodes);
                messagesListView.getItems().setAll(currentItems);
                if (!currentItems.isEmpty()) {
                    messagesListView.scrollTo(currentItems.size() - 1);
                }
            } else {
                removeAllPollsFromChat();
            }
        } catch (RuntimeException e) {
            showAlert("Erreur", "Erreur lors du chargement des sondages : " + e.getMessage());
        }
    }

    private VBox createPollNode(Poll poll) {
        List<PollOption> options = poll.getOptions();
        if (options == null || options.isEmpty()) {
            System.out.println("Avertissement : Le sondage doit avoir au moins une option, trouvé " + (options == null ? 0 : options.size()) + " pour le sondage ID " + poll.getId());
            return null;
        }

        VBox pollNode = new VBox(5);
        pollNode.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 8; -fx-border-radius: 15; -fx-border-color: #BBDEFB;");
        pollNode.setAlignment(Pos.CENTER_LEFT);
        pollNode.setUserData(poll.getId());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.FRENCH);
        Label timeLabel = new Label("Créé à : " + sdf.format(poll.getCreatedAt()));
        timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
        pollNode.getChildren().add(timeLabel);

        Label questionLabel = new Label(poll.getQuestion());
        questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        pollNode.getChildren().add(questionLabel);

        ToggleGroup toggleGroup = new ToggleGroup();
        for (PollOption option : options) {
            RadioButton radioButton = new RadioButton(option.getText() + " (" + option.getVoteCount() + " votes)");
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setUserData(option.getId());
            pollNode.getChildren().add(radioButton);
        }

        if (!poll.isClosed()) {
            Button voteBtn = new Button("Voter");
            voteBtn.setStyle("-fx-background-color: #0078ff; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 2 10;");
            voteBtn.setOnAction(e -> handleVote(poll.getId(), toggleGroup));
            pollNode.getChildren().add(voteBtn);

            Button closeBtn = new Button("Fermer le sondage");
            closeBtn.setStyle("-fx-background-color: #FF4500; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 2 10;");
            closeBtn.setOnAction(e -> handleClosePoll(poll.getId()));
            pollNode.getChildren().add(closeBtn);
        } else {
            Label closedLabel = new Label("Fermé");
            closedLabel.setStyle("-fx-font-size: 10; -fx-text-fill: red;");
            pollNode.getChildren().add(closedLabel);
        }

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 2 10;");
        deleteBtn.setOnAction(e -> handleDeletePoll(poll.getId()));
        pollNode.getChildren().add(deleteBtn);

        return pollNode;
    }

    private void removeAllPollsFromChat() {
        List<Object> currentItems = new ArrayList<>(messagesListView.getItems());
        currentItems.removeIf(item -> item instanceof VBox);
        messagesListView.getItems().setAll(currentItems);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Information") || title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private class MessageCell extends ListCell<Object> {
        private VBox messageVBox = new VBox(5);
        private Label messageLabel = new Label();
        private Button editButton = new Button();
        private Button deleteButton = new Button();
        private TextField editTextField = new TextField();
        private Button saveButton = new Button();
        private Button cancelButton = new Button();
        private HBox buttonBox = new HBox(5);
        private HBox editBox = new HBox(5);
        private HBox reactionBox = new HBox(5);
        private Label reactionCountsLabel = new Label();
        private Object currentItem;

        public MessageCell() {
            FontIcon editIcon = new FontIcon(FontAwesomeRegular.EDIT);
            FontIcon deleteIcon = new FontIcon(FontAwesomeRegular.TRASH_ALT);
            FontIcon saveIcon = new FontIcon(FontAwesomeRegular.CHECK_CIRCLE);
            FontIcon cancelIcon = new FontIcon(FontAwesomeRegular.TIMES_CIRCLE);
            editIcon.setIconColor(Color.AQUAMARINE);
            deleteIcon.setIconColor(Color.AQUAMARINE);
            saveIcon.setIconColor(Color.AQUAMARINE);
            cancelIcon.setIconColor(Color.AQUAMARINE);
            saveIcon.setIconSize(18);
            cancelIcon.setIconSize(18);

            editButton.setGraphic(editIcon);
            deleteButton.setGraphic(deleteIcon);
            saveButton.setGraphic(saveIcon);
            cancelButton.setGraphic(cancelIcon);

            String[] emojis = {"👍", "❤️", "😂", "😮", "👏"};
            for (String emoji : emojis) {
                Button reactionButton = new Button(emoji);
                reactionButton.setStyle("-fx-background-color: #E26ECC; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 6; -fx-font-size: 12; -fx-cursor: hand;");
                reactionButton.setOnAction(e -> handleReaction(emoji));
                reactionBox.getChildren().add(reactionButton);
            }

            messageLabel.getStyleClass().add("chat-message");
            editTextField.getStyleClass().add("edit-field");
            reactionCountsLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");

            Stream.of(editButton, deleteButton, saveButton, cancelButton).forEach(btn -> {
                btn.getStyleClass().add("icon-button");
                btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            });

            buttonBox.getChildren().addAll(editButton, deleteButton);
            editBox.getChildren().addAll(editTextField, saveButton, cancelButton);
            editBox.setSpacing(5);
        }

        private void handleReaction(String emoji) {
            if (!(currentItem instanceof Message)) return;
            Message message = (Message) currentItem;

            try {
                boolean hasReacted = reactionService.getAll().stream()
                        .anyMatch(r -> r.getMessageId() == message.getId() &&
                                r.getUserId() == currentUserId &&
                                r.getEmoji().equals(emoji));

                if (hasReacted) {
                    Reaction reaction = reactionService.getAll().stream()
                            .filter(r -> r.getMessageId() == message.getId() &&
                                    r.getUserId() == currentUserId &&
                                    r.getEmoji().equals(emoji))
                            .findFirst()
                            .orElse(null);
                    if (reaction != null) {
                        reactionService.delete(reaction);
                    }
                } else {
                    Reaction reaction = new Reaction(
                            currentUserId,
                            message.getId(),
                            emoji,
                            new Timestamp(System.currentTimeMillis())
                    );
                    reactionService.add(reaction);
                }
                loadMessagesForChat(selectedChat.getId());
            } catch (RuntimeException e) {
                showAlert("Erreur", "Échec du traitement de la réaction : " + e.getMessage());
            }
        }

        private void handleDelete() {
            if (!(currentItem instanceof Message)) return;
            Message message = (Message) currentItem;

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmer la suppression");
            confirmation.setHeaderText(null);
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce message ?");
            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    messageService.delete(message);
                    getListView().getItems().remove(currentItem);
                    showAlert("Succès", "Message supprimé avec succès.");
                } catch (RuntimeException e) {
                    showAlert("Erreur", "Erreur lors de la suppression du message : " + e.getMessage());
                }
            }
        }

        private void handleEdit() {
            if (!(currentItem instanceof Message)) return;
            Message message = (Message) currentItem;
            editTextField.setText(message.getContenu());
            messageVBox.getChildren().setAll(editBox);
        }

        private void handleSave() {
            String newContent = editTextField.getText().trim();
            if (newContent.isEmpty() || !(currentItem instanceof Message)) {
                showAlert("Erreur", "Le message modifié ne peut pas être vide.");
                return;
            }
            try {
                Message message = (Message) currentItem;
                message.setContenu(newContent);
                messageService.update(message);
                messageLabel.setText(newContent);
                loadMessagesForChat(selectedChat.getId());
                showAlert("Succès", "Message modifié avec succès.");
            } catch (RuntimeException e) {
                showAlert("Erreur", "Erreur lors de la modification du message : " + e.getMessage());
            }
        }

        private void handleCancel() {
            if (selectedChat != null) {
                loadMessagesForChat(selectedChat.getId());
            }
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                currentItem = null;
            } else if (item instanceof Message) {
                Message message = (Message) item;
                currentItem = message;
                messageLabel.setText(message.getContenu());

                try {
                    Map<String, Long> reactionCounts = reactionService.getReactionCounts(message.getId());
                    StringBuilder countsText = new StringBuilder();
                    reactionCounts.forEach((emoji, count) -> {
                        if (count > 0) {
                            countsText.append(emoji).append(" ").append(count).append(" ");
                        }
                    });
                    reactionCountsLabel.setText(countsText.toString());
                } catch (RuntimeException e) {
                    reactionCountsLabel.setText("");
                    System.out.println("Erreur lors du chargement des réactions : " + e.getMessage());
                }

                configureMessageAppearance(message);

                messageVBox.getChildren().clear();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.FRENCH);
                String timeFormatted = sdf.format(message.getPostTime());
                Label chatNameLabel = new Label("Utilisateur " + message.getPostedBy());
                chatNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: gray;");
                Label timeLabel = new Label(timeFormatted);
                timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");

                if (message.getPostedBy() == -1) {
                    messageVBox.getChildren().addAll(
                            chatNameLabel,
                            new TextFlow(new Text(message.getContenu())),
                            timeLabel
                    );
                } else {
                    messageVBox.getChildren().addAll(
                            chatNameLabel,
                            new TextFlow(new Text(message.getContenu())),
                            timeLabel,
                            reactionBox,
                            reactionCountsLabel
                    );
                    if (message.getPostedBy() == currentUserId) {
                        messageVBox.getChildren().add(buttonBox);
                    }
                }

                setGraphic(messageVBox);
            } else if (item instanceof VBox) {
                VBox pollNode = (VBox) item;
                currentItem = pollNode;
                setGraphic(pollNode);
            }
        }

        private void configureMessageAppearance(Message message) {
            messageLabel.getStyleClass().removeAll("user-message", "other-user-message", "bot-message");
            messageVBox.setAlignment(message.getPostedBy() == currentUserId ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            if (message.getPostedBy() == -1) {
                messageLabel.getStyleClass().add("bot-message");
            } else if (message.getPostedBy() == currentUserId) {
                messageLabel.getStyleClass().add("user-message");
            } else {
                messageLabel.getStyleClass().add("other-user-message");
            }
        }
    }
}