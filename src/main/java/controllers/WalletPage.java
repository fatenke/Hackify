package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import models.wallet;
import services.WalletServices;
import utils.SessionManager;

import java.sql.SQLException;

public class WalletPage {
    @FXML
    private Label balanceLabel;
    @FXML
    private Button addPointsButton;
    @FXML
    private Button deductPointsButton;
    @FXML
    private Label lastTransactionLabel;

    private WalletServices walletServices;
    private int userId; // The ID of the logged-in user

    private static final float POINTS_PER_CLICK = 10; // Fixed amount of points to add/deduct

    public void initialize() {
        // Initialize wallet service
        System.out.println("WalletPage initialized!");
        walletServices = new WalletServices();
        userId = SessionManager.getSession(SessionManager.getLastSessionId()).getId();
        // Get the logged-in user's ID
        System.out.println("User ID: " + userId);

        // Add actions to the buttons
        addPointsButton.setOnAction(event -> addPoints());
        deductPointsButton.setOnAction(event -> deductPoints());

        // Load wallet data
        loadWalletData();
    }

    private void loadWalletData() {
        try {
            wallet userWallet = walletServices.getWalletByUserId(userId);
            if (userWallet != null) {
                System.out.println("Wallet found: " + userWallet.getBalance()); // Debugging
                updateUI(userWallet);
            } else {
                System.out.println("No wallet found for user ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void updateUI(wallet wallet) {
        balanceLabel.setText("Balance: " + wallet.getBalance() + " Points");
        lastTransactionLabel.setText("Last Transaction: " + wallet.getLastTransaction());
    }

    @FXML
    private void addPoints() {
        try {
            wallet userWallet = walletServices.getWalletByUserId(userId);
            if (userWallet != null) {
                userWallet.setBalance(userWallet.getBalance() + POINTS_PER_CLICK);
                userWallet.setLastTransaction("Added " + POINTS_PER_CLICK + " Points");
                walletServices.modifier(userWallet);
                updateUI(userWallet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while updating the wallet.");
        }
    }

    @FXML
    private void deductPoints() {
        try {
            wallet userWallet = walletServices.getWalletByUserId(userId);
            if (userWallet != null) {
                if (userWallet.getBalance() >= POINTS_PER_CLICK) {
                    userWallet.setBalance(userWallet.getBalance() - POINTS_PER_CLICK);
                    userWallet.setLastTransaction("Deducted " + POINTS_PER_CLICK + " Points");
                    walletServices.modifier(userWallet);
                    updateUI(userWallet);
                } else {
                    showAlert("Insufficient Balance", "You do not have enough points to deduct.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while updating the wallet.");
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
