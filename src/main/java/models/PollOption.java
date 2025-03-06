package models;

public class PollOption {
    private int id;
    private int pollId; // Links to the parent poll
    private String text; // e.g., "Idea A"
    private int voteCount; // Number of votes for this option

    // Constructor, getters, setters
    public PollOption(int id, int pollId, String text) {
        this.id = id;
        this.pollId = pollId;
        this.text = text;
        this.voteCount = 0;
    }

    public PollOption(int pollId, String text, int voteCount) {
        this.pollId = pollId;
        this.text = text;
        this.voteCount = voteCount;
    }

    public PollOption() {
    }

    public PollOption(int id, int pollId, String text, int voteCount) {
    }

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
