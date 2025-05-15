package models;

public class PollOption {
    private int id;
    private int pollId; // Foreign key to poll
    private String text; // Option text
    private int voteCount; // Number of votes

    // Default constructor
    public PollOption() {
    }

    // Constructor for creating a new option
    public PollOption(int pollId, String text) {
        this.pollId = pollId;
        this.text = text;
        this.voteCount = 0;
    }

    // Constructor for retrieving from database
    public PollOption(int id, int pollId, String text, int voteCount) {
        this.id = id;
        this.pollId = pollId;
        this.text = text;
        this.voteCount = voteCount;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}