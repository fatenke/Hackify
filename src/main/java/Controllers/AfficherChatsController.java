package Controllers;

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
import models.ChatType;
import models.Message;
import models.Message.MessageType;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;
import services.BotService;
import services.ChatService;
import services.GeminiService;
import services.MessageService;
import util.MyConnection;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AfficherChatsController {

    @FXML private Label chatTitle;
    @FXML private ListView<String> chatsListView;
    @FXML private ListView<Message> messagesListView;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private Button returnButton;
    @FXML private TextField searchField;
    @FXML private ListView<Message> searchResultsListView;
    @FXML private Label noResultsLabel;
    @FXML private HBox suggestionBox;

    // Poll-related FXML elements
    @FXML private TextField pollQuestionField;
    @FXML private TextField pollOption1Field;
    @FXML private TextField pollOption2Field;
    @FXML private Button createPollButton;
    @FXML private Label pollQuestionLabel;
    @FXML private VBox pollOptionsBox;
    @FXML private VBox pollDisplayBox;
    @FXML private Button voteButton;

    private ChatService chatService;
    private MessageService messageService;
    private GeminiService geminiService;
    private BotService botService;
    private int currentUserId = 6; // Should come from session management
    private boolean suggestionTriggered = false;
    private int currentCommunityId;
    private Chat selectedChat;
    private ToggleGroup pollToggleGroup = new ToggleGroup();

    private static final String[] SUGGESTIONS = {
            "How to join a hackathon?",
            "Where to find resources?",
            "where can i find my wallet  ?"
    };

    public void setCurrentCommunityId(int id) {
        this.currentCommunityId = id;
        loadChats();
    }

    @FXML
    public void initialize() {
        chatService = new ChatService();
        geminiService = new GeminiService("AIzaSyAdtU0BkTPvbpbKhK1J6AGNaSwaywhByZc");
        messageService = new MessageService(
                MyConnection.getInstance().getCnx(),
                chatService,
                geminiService
        );

        messagesListView.setCellFactory(lv -> new MessageCell());
        displaySuggestions();

        searchResultsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String chatName = "Unknown Chat";
                    Chat chat = chatService.getChatById(item.getChatId());
                    if (chat != null) {
                        chatName = chat.getNom();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
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
                    chatNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
                    Label timeLabel = new Label(timeFormatted + " - User: " + item.getPostedBy());
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

        loadActivePoll(); // Load poll on initialization
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
        List<Message> results = messageService.searchMessages(term);
        if (results.isEmpty()) {
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
            if (messagesListView.getItems().get(i).getId() == selectedMessage.getId()) {
                return i;
            }
        }
        return -1;
    }

    private void loadChats() {
        chatsListView.getItems().clear();
        List<Chat> communityChats = chatService.getChatsByCommunityId(currentCommunityId);
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
                        loadActivePoll(); // Reload poll when chat changes
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
        List<Message> chatMessages = messageService.getMessagesByChatId(chatId);
        List<Message> normalMessages = chatMessages.stream()
                .filter(m -> m.getType() != MessageType.SUGGESTION)
                .collect(Collectors.toList());
        messagesListView.getItems().setAll(normalMessages);
        displaySuggestions();
    }

    private void handleSuggestionClick(String suggestionText) {
        messageField.setText(suggestionText);
        handleSendMessage();
    }

    @FXML
    private void handleSendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty() || selectedChat == null) return;
        Message newMessage = new Message(
                selectedChat.getId(),
                content,
                MessageType.QUESTION,
                new Timestamp(System.currentTimeMillis()),
                currentUserId
        );
        messageService.add(newMessage);
        messageField.clear();
        loadMessagesForChat(selectedChat.getId());
        if (!suggestionTriggered) {
            displaySuggestions();
        } else {
            suggestionTriggered = false;
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
            e.printStackTrace();
        }
    }

    private void displaySuggestions() {
        if (selectedChat == null || selectedChat.getType() != ChatType.BOT_SUPPORT) {
            suggestionBox.getChildren().clear();
            return;
        }
        suggestionBox.getChildren().clear();
        for (String suggestion : SUGGESTIONS) {
            Button suggestionBtn = new Button(suggestion);
            suggestionBtn.setStyle("-fx-background-color: #4A148C; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 5 10; -fx-cursor: hand;");
            suggestionBtn.setOnAction(e -> handleSuggestionClick(suggestion));
            suggestionBox.getChildren().add(suggestionBtn);
        }
    }

    // Poll-related methods
    @FXML
    private void handleCreatePoll() {
        String question = pollQuestionField.getText().trim();
        String option1 = pollOption1Field.getText().trim();
        String option2 = pollOption2Field.getText().trim();

        if (question.isEmpty() || option1.isEmpty() || option2.isEmpty() || selectedChat == null) {
            return; // Basic validation
        }

        try {
            Connection conn = MyConnection.getInstance().getCnx();
            // Insert poll
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO polls (chat_id, question) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, selectedChat.getId());
            ps.setString(2, question);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            int pollId = rs.next() ? rs.getInt(1) : -1;

            // Insert options
            ps = conn.prepareStatement("INSERT INTO poll_options (poll_id, text) VALUES (?, ?)");
            ps.setInt(1, pollId);
            ps.setString(2, option1);
            ps.executeUpdate();
            ps.setString(2, option2);
            ps.executeUpdate();

            // Clear fields and refresh poll display
            pollQuestionField.clear();
            pollOption1Field.clear();
            pollOption2Field.clear();
            loadActivePoll();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to create poll: " + e.getMessage());
        }
    }

    @FXML
    private void handleVote() {
        RadioButton selectedOption = (RadioButton) pollToggleGroup.getSelectedToggle();
        if (selectedOption == null || selectedChat == null) return;

        int optionId = (int) selectedOption.getUserData();

        try {
            Connection conn = MyConnection.getInstance().getCnx();
            int pollId = getCurrentPollId();

            // Check if user already voted
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id FROM votes WHERE poll_id = ? AND user_id = ?");
            ps.setInt(1, pollId);
            ps.setInt(2, currentUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                showAlert("Info", "You have already voted in this poll.");
                return;
            }

            // Record vote
            ps = conn.prepareStatement("INSERT INTO votes (poll_id, option_id, user_id) VALUES (?, ?, ?)");
            ps.setInt(1, pollId);
            ps.setInt(2, optionId);
            ps.setInt(3, currentUserId);
            ps.executeUpdate();

            // Update vote count
            ps = conn.prepareStatement("UPDATE poll_options SET vote_count = vote_count + 1 WHERE id = ?");
            ps.setInt(1, optionId);
            ps.executeUpdate();

            loadActivePoll(); // Refresh display
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to record vote: " + e.getMessage());
        }
    }

    private void loadActivePoll() {
        if (selectedChat == null) {
            pollOptionsBox.getChildren().clear();
            pollQuestionLabel.setText("");
            pollDisplayBox.setVisible(false);
            return;
        }

        pollOptionsBox.getChildren().clear();
        try {
            Connection conn = MyConnection.getInstance().getCnx();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT p.id, p.question, po.id AS option_id, po.text, po.vote_count " +
                            "FROM polls p LEFT JOIN poll_options po ON p.id = po.poll_id " +
                            "WHERE p.chat_id = ? AND p.is_closed = FALSE ORDER BY p.created_at DESC LIMIT 1");
            ps.setInt(1, selectedChat.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                pollQuestionLabel.setText(rs.getString("question"));
                do {
                    RadioButton option = new RadioButton(rs.getString("text") + " (" + rs.getInt("vote_count") + " votes)");
                    option.setToggleGroup(pollToggleGroup);
                    option.setUserData(rs.getInt("option_id"));
                    pollOptionsBox.getChildren().add(option);
                } while (rs.next());
                pollDisplayBox.setVisible(true);
            } else {
                pollDisplayBox.setVisible(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load poll: " + e.getMessage());
        }
    }

    private int getCurrentPollId() {
        try {
            Connection conn = MyConnection.getInstance().getCnx();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id FROM polls WHERE chat_id = ? AND is_closed = FALSE ORDER BY created_at DESC LIMIT 1");
            ps.setInt(1, selectedChat.getId());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Custom ListCell for displaying Message objects with Edit and Delete options for user id 6.
    private class MessageCell extends ListCell<Message> {
        private HBox container = new HBox(10);
        private Label messageLabel = new Label();
        private Button editButton = new Button();
        private Button deleteButton = new Button();
        private TextField editTextField = new TextField();
        private Button saveButton = new Button();
        private Button cancelButton = new Button();
        private HBox buttonBox = new HBox(5);
        private HBox editBox = new HBox(5);
        private Message currentMessage;

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

            container.setAlignment(Pos.CENTER_RIGHT);
            messageLabel.getStyleClass().add("chat-message");
            editTextField.getStyleClass().add("edit-field");

            Stream.of(editButton, deleteButton, saveButton, cancelButton).forEach(btn -> {
                btn.getStyleClass().add("icon-button");
                btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            });

            buttonBox.getChildren().addAll(editButton, deleteButton);
            editBox.getChildren().addAll(editTextField, saveButton, cancelButton);
            editBox.setSpacing(5);
            container.getChildren().addAll(messageLabel, buttonBox);

            deleteButton.setOnAction(e -> handleDelete());
            editButton.setOnAction(e -> handleEdit());
            saveButton.setOnAction(e -> handleSave());
            cancelButton.setOnAction(e -> handleCancel());
        }

        private void handleDelete() {
            if (currentMessage != null) {
                messageService.delete(currentMessage);
                getListView().getItems().remove(currentMessage);
            }
        }

        private void handleEdit() {
            editTextField.setText(currentMessage.getContenu());
            container.getChildren().setAll(editBox);
        }

        private void handleSave() {
            String newContent = editTextField.getText().trim();
            if (!newContent.isEmpty()) {
                currentMessage.setContenu(newContent);
                messageService.update(currentMessage);
                messageLabel.setText(newContent);
                container.getChildren().setAll(messageLabel, buttonBox);
            }
        }

        private void handleCancel() {
            container.getChildren().setAll(messageLabel, buttonBox);
        }

        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                currentMessage = null;
            } else {
                currentMessage = item;
                configureMessageAppearance(item);
                configureButtons(item);
                setGraphic(container);
            }
        }

        private void configureMessageAppearance(Message message) {
            messageLabel.setText(message.getContenu());
            messageLabel.getStyleClass().removeAll("user-message", "other-user-message", "bot-message");

            if (message.getPostedBy() == -1) {
                messageLabel.getStyleClass().add("bot-message");
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().setAll(messageLabel);
            } else if (message.getPostedBy() == currentUserId) {
                messageLabel.getStyleClass().add("user-message");
                container.setAlignment(Pos.CENTER_RIGHT);
                container.getChildren().setAll(buttonBox, messageLabel);
            } else {
                messageLabel.getStyleClass().add("other-user-message");
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().setAll(messageLabel);
            }
        }

        private void configureButtons(Message message) {
            buttonBox.getChildren().clear();
            if (message.getType() == MessageType.QUESTION &&
                    message.getPostedBy() == currentUserId) {
                buttonBox.getChildren().addAll(editButton, deleteButton);
            }
        }
    }
}