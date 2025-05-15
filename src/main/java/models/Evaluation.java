

package models;

public class Evaluation {
    int id;
    int idJury;
    int idProjet;
    int idHackathon;
    float noteTech;
    float noteInnov;
    String date;

    public Evaluation() {
    }

    public Evaluation(int id, int idJury, int idHackathon, int idProjet, float noteTech, float noteInnov, String date) {
        this.id = id;
        this.noteTech = noteTech;
        this.date = date;
        this.noteInnov = noteInnov;
        this.idJury = idJury;
        this.idProjet = idProjet;
        this.noteInnov = noteInnov;
    }

    public Evaluation(float noteTech, float noteInnov, String date, int idJury, int idProjet, int idHackathon) {
        this.noteTech = noteTech;
        this.noteInnov = noteInnov;
        this.date = date;
        this.idJury = idJury;
        this.idProjet = idProjet;
        this.idHackathon = idHackathon;
    }

    public int getIdHackathon() {
        return this.idHackathon;
    }

    public void setIdHackathon(int idHackathon) {
        this.idHackathon = idHackathon;
    }

    public int getIdJury() {
        return this.idJury;
    }

    public void setIdJury(int idJury) {
        this.idJury = idJury;
    }

    public int getIdProjet() {
        return this.idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getNoteInnov() {
        return this.noteInnov;
    }

    public void setNoteInnov(float noteInnov) {
        this.noteInnov = noteInnov;
    }

    public float getNoteTech() {
        return this.noteTech;
    }

    public void setNoteTech(float noteTech) {
        this.noteTech = noteTech;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString() {
        return "id=" + this.id + ", idJury=" + this.idJury + ", idProjet=" + this.idProjet + ", idHackathon=" + this.idHackathon + ", noteTech=" + this.noteTech + ", noteInnov=" + this.noteInnov + ", date='" + this.date;
    }
}
