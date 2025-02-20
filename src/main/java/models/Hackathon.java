package models;

import java.time.LocalDateTime;
import java.util.Date;

public class Hackathon {
    private int id_hackathon;
    private int id_organisateur;
    private String nom_hackathon;
    private String description;
    private LocalDateTime date_debut;
    private LocalDateTime date_fin;
    private String lieu;
    private String theme;
    private String conditions_participation;

    public Hackathon() {
    }

    public Hackathon(int id_organisateur,String nom_hackathon,String description,String theme,LocalDateTime date_debut, LocalDateTime date_fin,String lieu,String conditions_participation) {
        this.id_organisateur=id_organisateur;
        this.nom_hackathon=nom_hackathon;
        this.description=description;
        this.theme= theme;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.lieu=lieu;
        this.conditions_participation=conditions_participation;
    }
    public Hackathon(int id_hackathon,int id_organisateur,String nom_hackathon,String description,String theme,LocalDateTime date_debut, LocalDateTime date_fin,String lieu,String conditions_participation) {
        this.id_hackathon=id_hackathon;
        this.id_organisateur=id_organisateur;
        this.nom_hackathon=nom_hackathon;
        this.description=description;
        this.theme= theme;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.lieu=lieu;
        this.conditions_participation=conditions_participation;
    }
    public Hackathon(String nom_hackathon,String description,String theme,LocalDateTime date_debut, LocalDateTime date_fin,String lieu,String conditions_participation) {
        this.nom_hackathon=nom_hackathon;
        this.description=description;
        this.theme= theme;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.lieu=lieu;
        this.conditions_participation=conditions_participation;

    }

    public int getId_hackathon() {
        return id_hackathon;
    }

    public String getNom_hackathon() {
        return nom_hackathon;
    }

    public String getDescription() {
        return description;
    }

    public int getId_organisateur() {
        return id_organisateur;
    }

    public LocalDateTime getDate_debut() {
        return date_debut;
    }

    public LocalDateTime getDate_fin() {
        return date_fin;
    }

    public String getLieu() {
        return lieu;
    }

    public String getTheme() {
        return theme;
    }

    public String getConditions_participation() {
        return conditions_participation;
    }

    public void setNom_hackathon(String nom_hackathon) {
        this.nom_hackathon = nom_hackathon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setConditions_participation(String conditions_participation) {
        this.conditions_participation = conditions_participation;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setDate_debut(LocalDateTime date_debut) {
        this.date_debut = date_debut;
    }

    public void setDate_fin(LocalDateTime date_fin) {
        this.date_fin = date_fin;
    }

    @Override
    public String toString() {
        return "Hackathon{" +
                "\nid_organisateur=" + id_organisateur +
                "\nnom_hackathon='" + nom_hackathon +
                "\ndescription='" + description +
                "\ndate_debut=" + date_debut +
                "\ndate_fin=" + date_fin +
                "\nlieu='" + lieu +
                "\ntheme='" + theme +
                "\nconditions_participation='" + conditions_participation +
                '}';
    }


}
