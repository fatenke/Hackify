package models;

import java.sql.Timestamp;
import java.util.List;

public class Poll {
    private int id;
    private int chatId; // Foreign key to chat
    private String question; // Poll question
    private List<PollOption> options; // List of poll options
    private boolean isClosed; // Whether the poll is closed
    private Timestamp createdAt; // Creation timestamp

    // Default constructor
    public Poll() {
    }

    // Constructor for creating a new poll
    public Poll(int chatId, String question, List<PollOption> options) {
        this.chatId = chatId;
        this.question = question;
        this.options = options;
        this.isClosed = false;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for retrieving from database
    public Poll(int id, int chatId, String question, boolean isClosed, Timestamp createdAt) {
        this.id = id;
        this.chatId = chatId;
        this.question = question;
        this.isClosed = isClosed;
        this.createdAt = createdAt;
    }

    // Full constructor including options
    public Poll(int id, int chatId, String question, List<PollOption> options, boolean isClosed, Timestamp createdAt) {
        this.id = id;
        this.chatId = chatId;
        this.question = question;
        this.options = options;
        this.isClosed = isClosed;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options = options;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}