package services;

import models.wallet;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WalletServices implements IService<wallet> {
    private Connection connection;

    public WalletServices() {
        connection = MyConnection.getInstance().getCnx();

    }
    @Override
    public void ajouter(wallet wallet) throws SQLException {
        try {
            String requete = "INSERT INTO wallet (user_id, balance, lastTransaction) VALUES (?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(requete);
            // Using getUserId() to set the userId in the wallet
            ps.setInt(1, wallet.getUserId());
            ps.setFloat(2, wallet.getBalance());
            ps.setString(3, wallet.getLastTransaction());
            ps.executeUpdate();
            System.out.println("Portefeuille créé pour l'utilisateur " + wallet.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(wallet wallet) throws SQLException {
        String requete = "UPDATE wallet SET balance = ?, lastTransaction = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(requete);
        ps.setFloat(1, wallet.getBalance());
        ps.setString(2, wallet.getLastTransaction());
        ps.setInt(3, wallet.getWalletId());
        ps.executeUpdate();
        System.out.println("Wallet ID " + wallet.getWalletId() + " updated successfully!");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String requete = "DELETE FROM wallet WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(requete);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("Wallet ID " + id + " deleted successfully!");
    }

    @Override
    public List<wallet> recuperer() throws SQLException {
        List<wallet> wallets = new ArrayList<>();
        String requete = "SELECT * FROM wallet";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(requete);

        while (rs.next()) {
            wallet wallet = new wallet();
            wallet.setWalletId(rs.getInt("id"));
            wallet.setUserId(rs.getInt("user_id"));
            wallet.setBalance(rs.getFloat("balance"));
            wallet.setLastTransaction(rs.getString("lastTransaction"));
            wallets.add(wallet);
        }
        return wallets;
    }
    public wallet getWalletByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM wallet WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new wallet(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getFloat("balance"),
                    rs.getString("lastTransaction")
            );
        }
        return null;
    }

}
