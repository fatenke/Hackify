package models;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Poll {
    private int id;
    private int chatId; // Links to a specific chat (announcement, feedback, etc.)
    private String question; // e.g., "Which project idea is best?"
    private List<PollOption> options; // List of voting options
    private boolean isClosed; // Whether voting is still open
    private Date createdAt;

    // Constructor, getters, setters
    public Poll(int id, int chatId, String question, List<PollOption> options) {
        this.id = id;
        this.chatId = chatId;
        this.question = question;
        this.options = options;
        this.isClosed = false;
        this.createdAt = new Date();
    }

    public Poll() {
    }

    public Poll(int chatId, String question, List<PollOption> options, boolean isClosed, Date createdAt) {
        this.chatId = chatId;
        this.question = question;
        this.options = options;
        this.isClosed = isClosed;
        this.createdAt = createdAt;
    }

    public Poll(int id, int chatId, String question, Timestamp createdAt, boolean isClosed) {
    }

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

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public Timestamp getCreatedAt() {
        return (Timestamp) createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
