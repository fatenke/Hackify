package Controllers;

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
import models.ChatType;
import models.Message;
import models.Message.MessageType;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;
import services.*;
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
    @FXML private ListView<Object> messagesListView; // Changed to Object to handle both Messages and Poll nodes
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
    @FXML private Button cancelPollButton;
    @FXML private VBox pollCreationPopout;
    @FXML private Button pollIconButton;

    private GeminiService geminiService;
    private ChatService chatService;
    private MessageService messageService;
    private PollService pollService = new PollService();
    private PollOptionService pollOptionService = new PollOptionService();
    private VoteService voteService = new VoteService();
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
                geminiService // Assuming GeminiService is not needed for this example
        );

        messagesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else if (item instanceof Message) {
                    Message message = (Message) item;
                    String chatName = "Unknown Chat";
                    Chat chat = chatService.getChatById(message.getChatId());
                    if (chat != null) {
                        chatName = chat.getNom();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String timeFormatted = sdf.format(message.getPostTime());
                    TextFlow messageFlow = new TextFlow(new Text(message.getContenu()));
                    VBox vbox = new VBox(5);
                    Label chatNameLabel = new Label(chatName);
                    chatNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: gray;");
                    Label timeLabel = new Label(timeFormatted);
                    timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
                    vbox.getChildren().addAll(chatNameLabel, messageFlow, timeLabel);
                    configureMessageAppearance(vbox, message);
                    setGraphic(vbox);
                } else if (item instanceof VBox) {
                    setGraphic((VBox) item);
                    System.out.println("Poll node displayed: " + ((VBox) item).getChildren().get(0)); // Debug poll display
                }
            }

            private void configureMessageAppearance(VBox vbox, Message message) {
                vbox.getStyleClass().removeAll("user-message", "other-user-message", "bot-message");
                if (message.getPostedBy() == -1) {
                    vbox.getStyleClass().add("bot-message");
                    vbox.setAlignment(Pos.CENTER_LEFT);
                } else if (message.getPostedBy() == currentUserId) {
                    vbox.getStyleClass().add("user-message");
                    vbox.setAlignment(Pos.CENTER_RIGHT);
                } else {
                    vbox.getStyleClass().add("other-user-message");
                    vbox.setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

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
                    chatNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: gray;");
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

        loadActivePolls(); // Load polls on initialization
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
                        loadActivePolls(); // Load polls for the selected chat
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
        List<Object> items = new ArrayList<>(chatMessages.stream()
                .filter(m -> m.getType() != MessageType.SUGGESTION)
                .collect(Collectors.toList()));
        messagesListView.getItems().setAll(items);
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
            showAlert("Error", "Please fill all poll fields.");
            return;
        }

        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        int pollId = pollService.createPoll(selectedChat.getId(), question, createdAt);
        if (pollId != -1) {
            int opt1Id = pollOptionService.createOption(pollId, option1);
            int opt2Id = pollOptionService.createOption(pollId, option2);
            addPollToChat(pollId, question, option1, opt1Id, option2, opt2Id, createdAt, false);
        } else {
            showAlert("Error", "Failed to create poll.");
        }

        pollQuestionField.clear();
        pollOption1Field.clear();
        pollOption2Field.clear();
        handleCancelPoll();
        loadMessagesForChat(selectedChat.getId()); // Refresh to show the poll
    }

    @FXML
    private void handleCancelPoll() {
        pollCreationPopout.setVisible(false);
        pollCreationPopout.setManaged(false);
        pollQuestionField.clear();
        pollOption1Field.clear();
        pollOption2Field.clear();
    }

    private void handleVote(int pollId) {
        if (selectedChat == null) {
            showAlert("Error", "No chat selected.");
            return;
        }

        // Debug: Verify pollId
        System.out.println("Handling vote for poll ID: " + pollId);

        RadioButton selectedOption = (RadioButton) pollToggleGroup.getSelectedToggle();
        if (selectedOption == null) {
            showAlert("Error", "Please select an option to vote.");
            return;
        }

        int optionId = (int) selectedOption.getUserData();
        if (voteService.hasUserVoted(pollId, currentUserId)) {
            showAlert("Info", "You have already voted in this poll.");
            return;
        }

        if (pollService.isPollClosed(pollId)) {
            showAlert("Info", "This poll is closed and cannot accept votes.");
            return;
        }

        // Record the vote and update the vote count
        voteService.recordVote(pollId, optionId, currentUserId);
        pollOptionService.incrementVoteCount(optionId);

        // Refresh the poll display to show updated vote counts
        loadActivePolls();
    }

    private void handleClosePoll(int pollId) {
        if (selectedChat == null) {
            showAlert("Error", "No chat selected.");
            return;
        }

        // Debug: Verify pollId
        System.out.println("Handling close for poll ID: " + pollId);

        pollService.closePoll(pollId);
        loadActivePolls(); // Refresh UI
    }

    private void handleDeletePoll(int pollId) {
        if (selectedChat == null) {
            showAlert("Error", "No chat selected.");
            return;
        }

        // Debug: Verify pollId
        System.out.println("Handling delete for poll ID: " + pollId);

        // Delete the poll from the database
        pollService.deletePoll(pollId);

        // Refresh the poll display to remove the deleted poll
        loadActivePolls();
    }

    private int getPollIdFromNode(VBox pollNode) {
        if (pollNode != null) {
            System.out.println("Poll node userData: " + pollNode.getUserData());
            if (pollNode.getUserData() instanceof Integer) {
                return (int) pollNode.getUserData();
            }
        }
        return -1;
    }

    private void loadActivePolls() {
        if (selectedChat == null) {
            removeAllPollsFromChat();
            return;
        }

        try {
            Connection conn = MyConnection.getInstance().getCnx();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT p.id AS poll_id, p.question, p.is_closed, p.created_at, po.id AS option_id, po.text, po.vote_count " +
                            "FROM polls p " +
                            "LEFT JOIN poll_options po ON p.id = po.poll_id " +
                            "WHERE p.chat_id = ? AND p.is_closed = FALSE " + // Ensure only active polls
                            "ORDER BY p.created_at ASC");
            ps.setInt(1, selectedChat.getId());
            ResultSet rs = ps.executeQuery();

            System.out.println("Loading polls for chat " + selectedChat.getId() + ": ResultSet has data: " + rs.isBeforeFirst());
            List<VBox> pollNodes = new ArrayList<>();
            int currentPollId = -1;
            String question = null;
            Timestamp createdAt = null;
            boolean isClosed = false;
            List<String> options = new ArrayList<>();
            List<Integer> optionIds = new ArrayList<>();
            List<Integer> voteCounts = new ArrayList<>();

            while (rs.next()) {
                int pollId = rs.getInt("poll_id");
                if (pollId != currentPollId) {
                    if (currentPollId != -1 && !options.isEmpty()) {
                        addPollNode(pollNodes, currentPollId, question, options, optionIds, voteCounts, createdAt, isClosed);
                    }
                    currentPollId = pollId;
                    question = rs.getString("question");
                    isClosed = rs.getBoolean("is_closed");
                    createdAt = rs.getTimestamp("created_at");
                    options.clear();
                    optionIds.clear();
                    voteCounts.clear();
                }
                String optionText = rs.getString("text");
                int optionId = rs.getInt("option_id");
                int voteCount = rs.getInt("vote_count");
                if (optionText != null) {
                    options.add(optionText);
                    optionIds.add(optionId);
                    voteCounts.add(voteCount);
                    System.out.println("Poll ID: " + pollId + ", Option: " + optionText + ", Vote Count: " + voteCount);
                }
            }
            if (currentPollId != -1 && !options.isEmpty()) {
                addPollNode(pollNodes, currentPollId, question, options, optionIds, voteCounts, createdAt, isClosed);
            }

            if (!pollNodes.isEmpty()) {
                List<Object> currentItems = new ArrayList<>(messagesListView.getItems());
                currentItems.removeIf(item -> item instanceof VBox); // Remove old polls
                currentItems.addAll(pollNodes);
                messagesListView.getItems().setAll(currentItems);
                messagesListView.scrollTo(messagesListView.getItems().size() - 1);
                System.out.println("MessagesListView items after adding polls: " + messagesListView.getItems().stream().map(Object::toString).collect(Collectors.joining(", ")));
            } else {
                System.out.println("No active polls found for chat " + selectedChat.getId());
                removeAllPollsFromChat();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load polls: " + e.getMessage());
        }
    }

    private void addPollNode(List<VBox> pollNodes, int pollId, String question, List<String> options, List<Integer> optionIds, List<Integer> voteCounts, Timestamp createdAt, boolean isClosed) {
        if (options.size() != 2) {
            System.out.println("Warning: Poll should have exactly 2 options, found " + options.size() + " for poll ID " + pollId);
            return; // Skip invalid polls (only handle polls with 2 options)
        }

        VBox pollNode = new VBox(5);
        pollNode.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 8; -fx-border-radius: 15; -fx-border-color: #BBDEFB;");
        pollNode.setAlignment(Pos.CENTER_LEFT);
        pollNode.setUserData(pollId); // Store poll ID for later retrieval

        // Creation time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Label timeLabel = new Label("Created at: " + sdf.format(createdAt));
        timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
        pollNode.getChildren().add(timeLabel);

        // Poll question
        Label questionLabel = new Label(question);
        questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        pollNode.getChildren().add(questionLabel);

        // Poll options (single card with both options)
        pollToggleGroup = new ToggleGroup();
        for (int i = 0; i < options.size(); i++) {
            RadioButton option = new RadioButton(options.get(i) + " (" + voteCounts.get(i) + " votes)");
            option.setToggleGroup(pollToggleGroup);
            option.setUserData(optionIds.get(i)); // Store real option ID
            pollNode.getChildren().add(option);
        }

        // Vote button (if not closed)
        if (!isClosed) {
            Button voteBtn = new Button("Vote");
            voteBtn.setStyle("-fx-background-color: #0078ff; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 2 10;");
            voteBtn.setOnAction(e -> handleVote(pollId)); // Pass pollId directly
            pollNode.getChildren().add(voteBtn);

            // Close poll button
            Button closeBtn = new Button("Close Poll");
            closeBtn.setStyle("-fx-background-color: #FF4500; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 2 10;");
            closeBtn.setOnAction(e -> handleClosePoll(pollId)); // Pass pollId directly
            pollNode.getChildren().add(closeBtn);
        } else {
            Label closedLabel = new Label("Closed");
            closedLabel.setStyle("-fx-font-size: 10; -fx-text-fill: red;");
            pollNode.getChildren().add(closedLabel);
        }

        // Delete poll button
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 2 10;");
        deleteBtn.setOnAction(e -> handleDeletePoll(pollId)); // Pass pollId directly
        pollNode.getChildren().add(deleteBtn);

        pollNodes.add(pollNode);
    }

    private void addPollToChat(int pollId, String question, String option1, int opt1Id, String option2, int opt2Id, Timestamp createdAt, boolean isClosed) {
        VBox pollNode = new VBox(5);
        pollNode.setStyle("-fx-background-color: #E3F2FD; -fx-padding: 8; -fx-border-radius: 15; -fx-border-color: #BBDEFB;");
        pollNode.setAlignment(Pos.CENTER_LEFT);
        pollNode.setUserData(pollId); // Store poll ID for later retrieval

        // Creation time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Label timeLabel = new Label("Created at: " + sdf.format(createdAt));
        timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
        pollNode.getChildren().add(timeLabel);

        // Poll question
        Label questionLabel = new Label(question);
        questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        pollNode.getChildren().add(questionLabel);

        // Poll options (single card with both options)
        pollToggleGroup = new ToggleGroup();
        RadioButton opt1 = new RadioButton(option1 + " (" + pollOptionService.getVoteCount(opt1Id) + " votes)");
        opt1.setToggleGroup(pollToggleGroup);
        opt1.setUserData(opt1Id); // Store real option ID
        RadioButton opt2 = new RadioButton(option2 + " (" + pollOptionService.getVoteCount(opt2Id) + " votes)");
        opt2.setToggleGroup(pollToggleGroup);
        opt2.setUserData(opt2Id); // Store real option ID
        pollNode.getChildren().addAll(opt1, opt2);

        // Vote button (if not closed)
        if (!isClosed) {
            Button voteBtn = new Button("Vote");
            voteBtn.setStyle("-fx-background-color: #0078ff; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 2 10;");
            voteBtn.setOnAction(e -> handleVote(pollId)); // Pass pollId directly
            pollNode.getChildren().add(voteBtn);

            // Close poll button
            Button closeBtn = new Button("Close Poll");
            closeBtn.setStyle("-fx-background-color: #FF4500; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 2 10;");
            closeBtn.setOnAction(e -> handleClosePoll(pollId)); // Pass pollId directly
            pollNode.getChildren().add(closeBtn);
        } else {
            Label closedLabel = new Label("Closed");
            closedLabel.setStyle("-fx-font-size: 10; -fx-text-fill: red;");
            pollNode.getChildren().add(closedLabel);
        }

        // Delete poll button
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 2 10;");
        deleteBtn.setOnAction(e -> handleDeletePoll(pollId)); // Pass pollId directly
        pollNode.getChildren().add(deleteBtn);

        List<Object> currentItems = new ArrayList<>(messagesListView.getItems());
        currentItems.add(pollNode);
        messagesListView.getItems().setAll(currentItems);
        messagesListView.scrollTo(messagesListView.getItems().size() - 1);
    }

    private void removeAllPollsFromChat() {
        List<Object> currentItems = new ArrayList<>(messagesListView.getItems());
        currentItems.removeIf(item -> item instanceof VBox);
        messagesListView.getItems().setAll(currentItems);
    }

    private void configureMessageAppearance(VBox vbox, Message message) {
        vbox.getStyleClass().removeAll("user-message", "other-user-message", "bot-message");
        if (message.getPostedBy() == -1) {
            vbox.getStyleClass().add("bot-message");
            vbox.setAlignment(Pos.CENTER_LEFT);
        } else if (message.getPostedBy() == currentUserId) {
            vbox.getStyleClass().add("user-message");
            vbox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            vbox.getStyleClass().add("other-user-message");
            vbox.setAlignment(Pos.CENTER_LEFT);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Custom ListCell for displaying Message objects and Poll nodes with Messenger-like styling.
    private class MessageCell extends ListCell<Object> {
        private HBox container = new HBox(10);
        private Label messageLabel = new Label();
        private Button editButton = new Button();
        private Button deleteButton = new Button();
        private TextField editTextField = new TextField();
        private Button saveButton = new Button();
        private Button cancelButton = new Button();
        private HBox buttonBox = new HBox(5);
        private HBox editBox = new HBox(5);
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
            if (currentItem instanceof Message) {
                Message message = (Message) currentItem;
                messageService.delete(message);
                getListView().getItems().remove(currentItem);
            }
        }

        private void handleEdit() {
            if (currentItem instanceof Message) {
                Message message = (Message) currentItem;
                editTextField.setText(message.getContenu());
                container.getChildren().setAll(editBox);
            }
        }

        private void handleSave() {
            String newContent = editTextField.getText().trim();
            if (!newContent.isEmpty() && currentItem instanceof Message) {
                Message message = (Message) currentItem;
                message.setContenu(newContent);
                messageService.update(message);
                messageLabel.setText(newContent);
                container.getChildren().setAll(messageLabel, buttonBox);
            }
        }

        private void handleCancel() {
            container.getChildren().setAll(messageLabel, buttonBox);
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
                configureMessageAppearance(message); // Updated to work with messageLabel and container
                setGraphic(container);
            } else if (item instanceof VBox) {
                VBox pollNode = (VBox) item;
                currentItem = pollNode;
                setGraphic(pollNode);
            }
        }

        private void configureMessageAppearance(Message message) {
            // Remove existing style classes
            messageLabel.getStyleClass().removeAll("user-message", "other-user-message", "bot-message");

            // Configure alignment and style based on sender
            if (message.getPostedBy() == -1) { // Bot message
                messageLabel.getStyleClass().add("bot-message");
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().setAll(messageLabel); // No buttons for bot messages
            } else if (message.getPostedBy() == currentUserId) { // User's own message
                messageLabel.getStyleClass().add("user-message");
                container.setAlignment(Pos.CENTER_RIGHT);
                container.getChildren().setAll(messageLabel, buttonBox); // Include edit/delete buttons
            } else { // Other user's message
                messageLabel.getStyleClass().add("other-user-message");
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().setAll(messageLabel); // No buttons for other users' messages
            }
        }

        private void configureButtons(Message message) {
            buttonBox.getChildren().clear();
            if (message.getType() == MessageType.QUESTION && message.getPostedBy() == currentUserId) {
                buttonBox.getChildren().addAll(editButton, deleteButton);
            }
        }
    }
}