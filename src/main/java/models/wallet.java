package models;

public class wallet {
    private int walletId;
    private int userId;
    private float balance;
    private String lastTransaction;

    // Default constructor
    public wallet() {}

    // Constructor with userId and balance
    public wallet(int userId, float balance) {
        this.userId = userId;
        this.balance = balance;
        this.lastTransaction = "Wallet Created"; // Default message
    }

    // Constructor with all attributes
    public wallet(int userId, float balance, String lastTransaction) {
        this.userId = userId;
        this.balance = balance;
        this.lastTransaction = lastTransaction;
    }
    public wallet( float balance, String lastTransaction, int walletId) {
        this.balance = balance;
        this.lastTransaction = lastTransaction;
        this.walletId = walletId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(String lastTransaction) {
        this.lastTransaction = lastTransaction;
    }
    public String toString() {
        return "Wallet{" +
                "walletId=" + walletId +
                ", userId=" + userId +
                ", balance=" + balance +
                ", lastTransaction='" + lastTransaction + '\'' +
                '}';
    }
    }