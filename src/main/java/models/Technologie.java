package models;

public class Technologie {


    int id_tech;
    String nom_tech;
    String type_tech;
    String complexite;
    String documentaire;


    public Technologie() {}

    public Technologie(int id_tech, String nom_tech, String type_tech, String complexite, String documentaire) {
        this.id_tech = id_tech;
        this.nom_tech = nom_tech;
        this.type_tech = type_tech;
        this.complexite = complexite;
        this.documentaire = documentaire;
    }
    public Technologie( String nom_tech, String type_tech, String complexite, String documentaire) {
        this.nom_tech = nom_tech;
        this.type_tech = type_tech;
        this.complexite = complexite;
        this.documentaire = documentaire;
    }

    public int getId_tech() {
        return id_tech;
    }

    public void setId_tech(int id_tech) {
        this.id_tech = id_tech;
    }

    public String getNom_tech() {
        return nom_tech;
    }

    public void setNom_tech(String nom_tech) {
        this.nom_tech = nom_tech;
    }

    public String getType_tech() {
        return type_tech;
    }

    public void setType_tech(String type_tech) {
        this.type_tech = type_tech;
    }

    public String getComplexite() {
        return complexite;
    }

    public void setComplexite(String complexite) {
        this.complexite = complexite;
    }

    public String getDocumentaire() {
        return documentaire;
    }

    public void setDocumentaire(String documentaire) {
        this.documentaire = documentaire;
    }

    @Override
    public String toString() {
        return "Technologie{" +
                "id_tech=" + id_tech +
                ", nom_tech='" + nom_tech + '\'' +
                ", type_tech='" + type_tech + '\'' +
                ", complexite='" + complexite + '\'' +
                ", documentaire='" + documentaire + '\'' +
                '}';
    }
}
