package services;

import Interfaces.GlobalInterface;
import models.Hackathon;
import util.MyConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class HackathonService implements GlobalInterface<Hackathon> {

    private final Connection connection;

    public HackathonService() {
        this.connection = MyConnection.getInstance().getCnx();
    }

    @Override
    public void add(Hackathon hackathon) {
        String req = "INSERT INTO `hackathon`( `nom_hackathon`, `description`, `date_debut`, `date_fin`, `lieu`, `theme`, `conditions_participation`,`id_organisateur`) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(req);
            statement.setString(1, hackathon.getNom_hackathon());
            statement.setString(2, hackathon.getDescription());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(hackathon.getDate_debut()));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(hackathon.getDate_fin()));
            statement.setString(5, hackathon.getLieu());
            statement.setString(6, hackathon.getTheme());
            statement.setString(7, hackathon.getConditions_participation());
            statement.setInt(8, hackathon.getId_organisateur());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }

    @Override
    public void update(Hackathon hackathon) {

    }

    @Override
    public List<Hackathon> getAll() {
        return List.of();
    }

    @Override
    public void delete(Hackathon hackathon) {

    }
}
