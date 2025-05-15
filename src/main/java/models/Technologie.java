package models;

public class Technologie {

    int id;
    String nom_tech;
    String type_tech;
    String complexite;
    String documentaire;
    String compatibilite; // Changed to String instead of List<String>

    public Technologie() {}

    public Technologie(int id, String nom_tech, String type_tech, String complexite, String documentaire, String compatibilite) {
        this.id = id;
        this.nom_tech = nom_tech;
        this.type_tech = type_tech;
        this.complexite = complexite;
        this.documentaire = documentaire;
        this.compatibilite = compatibilite;
    }

    public Technologie(String nom_tech, String type_tech, String complexite, String documentaire, String compatibilite) {
        this.nom_tech = nom_tech;
        this.type_tech = type_tech;
        this.complexite = complexite;
        this.documentaire = documentaire;
        this.compatibilite = compatibilite;
    }

    public int getId_tech() {
        return id;
    }

    public void setId_tech(int id_tech) {
        this.id = id_tech;
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

    public String getCompatibilite() { // Updated getter for compatibilite as String
        return compatibilite;
    }

    public void setCompatibilite(String compatibilite) { // Updated setter for compatibilite as String
        this.compatibilite = compatibilite;
    }

    @Override
    public String toString() {
        return "Technologie{" +
                "id_tech=" + id +
                ", nom_tech='" + nom_tech + '\'' +
                ", type_tech='" + type_tech + '\'' +
                ", complexite='" + complexite + '\'' +
                ", documentaire='" + documentaire + '\'' +
                ", compatibilite='" + compatibilite + '\'' + // Updated toString for String type
                '}';
    }
}