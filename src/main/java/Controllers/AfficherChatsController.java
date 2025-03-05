package Controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AfficherChatsController {

    @FXML
    private Label chatTitle;

    @FXML
    private ListView<String> chatsListView;  // Sidebar: remains as String list for chat names.

    // Change messagesListView to hold Message objects.
    @FXML
    private ListView<Message> messagesListView;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;

    @FXML
    private Button returnButton;

    @FXML
    private HBox suggestionBox;
    private ChatService chatService;
    private MessageService messageService;
    private GeminiService geminiService;
    private  BotService botService;  // Inject BotService

    private int currentUserId = 6; // Should come from session management

    private boolean suggestionTriggered = false;

    private static final String[] SUGGESTIONS = {
            "How to join a hackathon?",
            "Where to find resources?",
            "where can i find my wallet  ?"

    };

    // The community ID passed from the previous view
    private int currentCommunityId;
    private Chat selectedChat;

    // Public setter to receive the community id
    public void setCurrentCommunityId(int id) {
        this.currentCommunityId = id;
        loadChats();
    }

    @FXML
    public void initialize() {
        // Initialize services with proper dependencies
        chatService = new ChatService();
        geminiService = new GeminiService("AIzaSyAdtU0BkTPvbpbKhK1J6AGNaSwaywhByZc");
        messageService = new MessageService(
                MyConnection.getInstance().getCnx(),
                chatService,
                geminiService
        );



        messagesListView.setCellFactory(lv -> new MessageCell());
        displaySuggestions();

    }


    private void loadChats() {
        chatsListView.getItems().clear();
        List<Chat> communityChats = chatService.getChatsByCommunityId(currentCommunityId);
        for (Chat c : communityChats) {
            chatsListView.getItems().add(c.getNom());
        }

        // Add a listener so that when a chat is selected, its messages are loaded.
        chatsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                for (Chat c : communityChats) {
                    if (c.getNom().equals(newVal)) {
                        selectedChat = c;
                        chatTitle.setText(c.getNom());
                        loadMessagesForChat(c.getId());
                        break;
                    }
                }
            }
    });

        // Optionally, select the first chat automatically.
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

        // Show suggestions immediately if chat is a chatbot
        displaySuggestions();
    }


    private void handleSuggestionClick(String suggestionText) {
        // When user clicks a suggestion, treat it as if they typed it
        messageField.setText(suggestionText);
        handleSendMessage(); // sends it
    }

    @FXML
    private void handleSendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty() || selectedChat == null) return;

        // Create and send user message
        Message newMessage = new Message(
                selectedChat.getId(),
                content,
                MessageType.QUESTION,
                new Timestamp(System.currentTimeMillis()),
                currentUserId // your dynamic user id
        );
        messageService.add(newMessage);
        messageField.clear();
        loadMessagesForChat(selectedChat.getId());

        if (!suggestionTriggered) {
            displaySuggestions();
        } else {
            suggestionTriggered = false;  // reset flag after processing
        }    }





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
            suggestionBox.getChildren().clear(); // Hide suggestions if not chatbot
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
            // Setup icons using FontAwesome
            FontIcon editIcon = new FontIcon(FontAwesomeRegular.EDIT);
            FontIcon deleteIcon = new FontIcon(FontAwesomeRegular.TRASH_ALT);
            FontIcon saveIcon = new FontIcon(FontAwesomeRegular.CHECK_CIRCLE);
            FontIcon cancelIcon = new FontIcon(FontAwesomeRegular.TIMES_CIRCLE);
            // Set icon colors
            editIcon.setIconColor(Color.AQUAMARINE);
            deleteIcon.setIconColor(Color.AQUAMARINE);
            saveIcon.setIconColor(Color.AQUAMARINE);
            cancelIcon.setIconColor(Color.AQUAMARINE);
            saveIcon.setIconSize(18);
            cancelIcon.setIconSize(18);

            // Assign icons to buttons
            editButton.setGraphic(editIcon);
            deleteButton.setGraphic(deleteIcon);
            saveButton.setGraphic(saveIcon);
            cancelButton.setGraphic(cancelIcon);

            // Style elements
            container.setAlignment(Pos.CENTER_RIGHT);
            messageLabel.getStyleClass().add("chat-message");
            editTextField.getStyleClass().add("edit-field");

            // Button styling
            Stream.of(editButton, deleteButton, saveButton, cancelButton).forEach(btn -> {
                btn.getStyleClass().add("icon-button");
                btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            });

            // Setup containers
            buttonBox.getChildren().addAll(editButton, deleteButton);
            editBox.getChildren().addAll(editTextField, saveButton, cancelButton);
            editBox.setSpacing(5);
            container.getChildren().addAll(messageLabel, buttonBox);

            // Event handlers
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
                // Bot message
                messageLabel.getStyleClass().add("bot-message");
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().setAll(messageLabel);
            } else if (message.getPostedBy() == currentUserId) {
                // Current user's message
                messageLabel.getStyleClass().add("user-message");
                container.setAlignment(Pos.CENTER_RIGHT);
                container.getChildren().setAll(buttonBox, messageLabel);
            } else {
                // Other community user's message
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
