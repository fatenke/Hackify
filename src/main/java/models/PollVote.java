package models;

public class PollVote {
    private int id;
    private int pollId; // Foreign key to poll
    private int optionId; // Foreign key to poll_option
    private int userId; // Foreign key to user

    // Default constructor
    public PollVote() {
    }

    // Constructor for creating a new vote
    public PollVote(int pollId, int optionId, int userId) {
        this.pollId = pollId;
        this.optionId = optionId;
        this.userId = userId;
    }

    // Constructor for retrieving from database
    public PollVote(int id, int pollId, int optionId, int userId) {
        this.id = id;
        this.pollId = pollId;
        this.optionId = optionId;
        this.userId = userId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}