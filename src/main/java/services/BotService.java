package services;

import models.Chat;
import models.Message;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BotService {
    static final int BOT_USER_ID = -1;
    private final MessageService messageService;
    private final ChatService chatService;
    private final GeminiService geminiService;

    public BotService(MessageService messageService, ChatService chatService, GeminiService geminiService) {
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
            System.err.println("Erreur BotService : " + e.getMessage());
            saveErrorResponse(userMessage);
        }
    }

    private boolean shouldRespond(Message message) {
        return message.getPostedBy() != BOT_USER_ID && isBotChat(message.getChatId());
    }

    private boolean isBotChat(int chatId) {
        Chat chat = chatService.getChatById(chatId);
        return chat != null && "BOT_SUPPORT".equals(chat.getType());
    }

    private String generateResponse(String userInput) {
        switch (userInput.toLowerCase()) {
            case "how to join a hackathon?":
                return "Pour rejoindre un hackathon, inscrivez-vous sur le site officiel de l'événement et suivez leurs instructions.";
            case "where to find resources?":
                return "Vous pouvez trouver des ressources pour les hackathons dans la section ressources où vous trouverez la documentation officielle et les communautés de codage.";
            case "can i quit a hackathon?":
                return "Il est généralement possible de quitter un hackathon, mais cela dépend des règles de l'événement. Contactez les organisateurs pour plus d'informations.";
            case "how to use wallet?":
                return "Votre portefeuille contient des pièces que vous gagnez ou perdez. Consultez la section portefeuille pour gérer vos fonds.";
            case "where can i find my wallet?":
                return "Vous pouvez trouver votre portefeuille dans votre profil. Avez-vous besoin d'une assistance supplémentaire ?";
            default:
                // For other inputs, use the Gemini API
                return geminiService.getResponse(userInput);
        }
    }

    private void saveResponse(Message original, String response) {
        messageService.add(new Message(
                original.getChatId(),
                response,
                new Timestamp(System.currentTimeMillis()),
                BOT_USER_ID
        ));
    }

    private void saveSuggestions(Message original, String response) {
        getSuggestions(response).forEach(suggestion ->
                messageService.add(new Message(
                        original.getChatId(),
                        suggestion,
                        new Timestamp(System.currentTimeMillis()),
                        BOT_USER_ID
                ))
        );
    }

    private List<String> getSuggestions(String response) {
        if (response.toLowerCase().contains("hackathon")) {
            return Arrays.asList(
                    "Comment me préparer pour un hackathon ?"
            );
        } else if (response.toLowerCase().contains("wallet")) {
            return Arrays.asList(
                    "Où puis-je trouver mon portefeuille ?",
                    "Comment utiliser mon portefeuille ?"
            );
        }
        return Collections.emptyList();
    }

    private void saveErrorResponse(Message original) {
        messageService.add(new Message(
                original.getChatId(),
                "Impossible de générer une réponse",
                new Timestamp(System.currentTimeMillis()),
                BOT_USER_ID
        ));
    }
}