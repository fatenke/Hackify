package models;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int chatId;
    private String contenu;
    private String type; // Changed from MessageType to String
    private Timestamp postTime;
    private int postedBy; // User ID of the sender



    public Message() {}

    public Message(int chatId, String contenu, Timestamp postTime, int postedBy) {
        this.chatId = chatId;
        this.contenu = contenu;
        this.postTime = postTime;
        this.postedBy = postedBy;
    }

    public Message(int id, int chatId, String contenu, Timestamp postTime, int postedBy) {
        this.id = id;
        this.chatId = chatId;
        this.contenu = contenu;
        this.postTime = postTime;
        this.postedBy = postedBy;
    }


    // Getters and Setters
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

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getPostTime() {
        return postTime;
    }

    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }

    public int getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(int postedBy) {
        this.postedBy = postedBy;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", contenu='" + contenu + '\'' +
                ", type='" + type + '\'' +
                ", postTime=" + postTime +
                ", postedBy=" + postedBy +
                '}';
    }
}