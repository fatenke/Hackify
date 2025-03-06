package models;

public class Vote {

    private int id;
    private int pollId;
    private int optionId;
    private int userId; // Who voted

    // Constructor, getters, setters
    public Vote(int id, int pollId, int optionId, int userId) {
        this.id = id;
        this.pollId = pollId;
        this.optionId = optionId;
        this.userId = userId;
    }

    public Vote() {
    }

    public Vote(int optionId, int pollId, int userId) {
        this.optionId = optionId;
        this.pollId = pollId;
        this.userId = userId;
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
