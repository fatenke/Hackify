package services;

import models.Chat;
import models.ChatType;
import models.Message;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BotService {
    // Changed to -1 to match database convention
    static  int BOT_USER_ID = -1;
    private final MessageService messageService;
    private final ChatService chatService;
    private final GeminiService geminiService;

    public BotService(MessageService messageService,
                      ChatService chatService,
                      GeminiService geminiService) {
        this.messageService = messageService;
        this.chatService = chatService;
        this.geminiService = geminiService;
    }

    public void handleUserMessage(Message userMessage) {
        if (!shouldRespond(userMessage)) return;

        try {
            String response = generateResponse(userMessage.getContenu());
            saveResponse(userMessage, response);
            saveSuggestions(userMessage, response);
        } catch (Exception e) {
            System.err.println("BotService error: " + e.getMessage());
            saveErrorResponse(userMessage);
        }
    }

    private boolean shouldRespond(Message message) {
        return message.getPostedBy() != BOT_USER_ID &&
                isBotChat(message.getChatId());
    }

    private boolean isBotChat(int chatId) {
        Chat chat = chatService.getChatById(chatId);
        return chat != null && chat.getType() == ChatType.BOT_SUPPORT;
    }
    private String generateResponse(String userInput) {
        switch (userInput.toLowerCase()) {
            case "how to join a hackathon?":
                return "To join a hackathon, register on the event's official website and follow their instructions.";
            case "where to find resources?":
                return "You can find hackathon resources on the resources section where you'll find official documentation, and coding communities.";
            case "can i quit a hackathon ?":
                return "No it's impossible to quit a Hackathon , if you need anything talk t";
            case "how to use wallet ?":
                return "your wallet has coins in them that you win or lose ";
            case "where can i find my wallet  ?":
                return "you can find your wallet in your profile , do you need further assistance ? ";

        }
        // For other inputs, use the Gemini API
        return geminiService.getResponse(userInput);
    }


    private void saveResponse(Message original, String response) {
        messageService.add(new Message(
                original.getChatId(),
                response,
                Message.MessageType.REPONSE,
                new Timestamp(System.currentTimeMillis()),
                BOT_USER_ID
        ));
    }
    private void saveSuggestions(Message original, String response) {
        getSuggestions(response).forEach(suggestion ->
                messageService.add(new Message(
                        original.getChatId(),
                        suggestion,
                        Message.MessageType.SUGGESTION,
                        new Timestamp(System.currentTimeMillis()),
                        BOT_USER_ID
                ))
        );
    }


    private List<String> getSuggestions(String response) {
        if (response.contains("hackathon")) {
            return Arrays.asList(
                    "How do I prepare for a hackathon?"
            );

        } else if (response.contains("wallet")) {
            return Arrays.asList(

                    "where can i find my wallet  ?",
                    "how to use wallet ?"
            );

        }
        return Collections.emptyList();
    }

    private void saveErrorResponse(Message original) {
        messageService.add(new Message(
                original.getChatId(),
                "Could not generate response",
                Message.MessageType.REPONSE,
                new Timestamp(System.currentTimeMillis()),
                BOT_USER_ID
        ));
    }
}