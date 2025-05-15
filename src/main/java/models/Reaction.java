package models;

import java.sql.Timestamp;

public class Reaction {
    private int id;
    private int userId; // ID of the user who reacted
    private int messageId; // ID of the message to which the reaction belongs
    private String emoji;
    private Timestamp createdAt; // Timestamp of when the reaction was created


    public Reaction(int id, int userId, int messageId, String emoji, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.messageId = messageId;
        this.emoji = emoji;
        this.createdAt = createdAt;
    }

    public Reaction(int userId, int messageId, String emoji, Timestamp createdAt) {
        this.userId = userId;
        this.messageId = messageId;
        this.emoji = emoji;
        this.createdAt = createdAt;
    }

    public Reaction() {
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Reaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", messageId=" + messageId +
                ", emoji='" + emoji + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    // Equals and hashCode methods can be added if needed for comparison in collections
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reaction)) return false;

        Reaction reaction = (Reaction) o;

        if (id != reaction.id) return false;
        if (userId != reaction.userId) return false;
        if (messageId != reaction.messageId) return false;
        if (!emoji.equals(reaction.emoji)) return false;
        return createdAt.equals(reaction.createdAt);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        result = 31 * result + messageId;
        result = 31 * result + emoji.hashCode();
        result = 31 * result + createdAt.hashCode();
        return result;
    }


}
