package models;

import java.sql.Timestamp;

public class Chat {
    private int id;
    private int communauteId;
    private String nom;
    private String type;
    private Timestamp dateCreation;
    private boolean isActive;

    // Default constructor
    public Chat() {
    }

    // Constructor for creating a new chat
    public Chat(int communauteId, String nom, String type) {
        this.communauteId = communauteId;
        this.nom = nom;
        this.type = type;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
        this.isActive = true;
    }

    // Constructor for retrieving from database
    public Chat(int id, int communauteId, String nom, String type, Timestamp dateCreation, boolean isActive) {
        this.id = id;
        this.communauteId = communauteId;
        this.nom = nom;
        this.type = type;
        this.dateCreation = dateCreation;
        this.isActive = isActive;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommunauteId() {
        return communauteId;
    }

    public void setCommunauteId(int communauteId) {
        this.communauteId = communauteId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", communauteId=" + communauteId +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", dateCreation=" + dateCreation +
                ", isActive=" + isActive +
                '}';
    }
}