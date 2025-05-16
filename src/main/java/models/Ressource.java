package models;

import java.util.Date;
import java.util.List;

public class Ressource {
    private int id;
    private String titre,description,type;
    private Date date_ajout;
    private boolean valide;


    // Constructeur par défaut
    public Ressource() {}

    // Constructeur avec paramètres
    public Ressource(int id, String titre, String type, String description, Date date_ajout, boolean valide)
    {
        this.id = id;
        this.titre = titre;
        this.type = type;
        this.description = description;
        this.date_ajout = date_ajout;
        this.valide = valide;

    }
    public Ressource(String titre, String type, String description, Date date_ajout , boolean valide) {
        this.titre = titre;
        this.type = type;
        this.description = description;
        this.date_ajout = date_ajout;
        this.valide = valide;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateAjout() {
        return date_ajout;
    }

    public void setDateAjout(Date date_ajout) {
        this.date_ajout = date_ajout;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }





    // Méthode d'affichage
    @Override
    public String toString() {
        return "Ressource{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ",date_ajout =" + date_ajout +
                ", valide=" + valide +

                '}';
    }
}