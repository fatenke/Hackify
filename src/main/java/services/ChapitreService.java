package services;

import models.Chapitre;
import util.MyConnection;
import Interfaces.IService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileNotFoundException;


public class ChapitreService implements IService<Chapitre> {
    private final Connection conn;

    public ChapitreService() {
        this.conn = MyConnection.getInstance().getConnection();
    }
    public boolean validateChapitre(Chapitre chapitre) {
        if (chapitre.getTitre() == null || chapitre.getTitre().trim().isEmpty()) {
            System.out.println("❌ Erreur : Le titre du chapitre est obligatoire !");
            return false;
        }
        if (chapitre.getContenu() == null || chapitre.getContenu().length() < 10) {
            System.out.println("❌ Erreur : Le contenu doit contenir au moins 10 caractères !");
            return false;
        }
        return true;
    }


    @Override
    public void ajouter(Chapitre chapitre) {
        String SQL = "INSERT INTO chapitres (id_ressources, titre, url_fichier, contenu, format_fichier) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = conn.prepareStatement(SQL);
            pst.setInt(1, chapitre.getIdRessource());
            pst.setString(2, chapitre.getTitre());
            pst.setString(3, chapitre.getUrlFichier());
            pst.setString(4, chapitre.getContenu());
            pst.setString(5, chapitre.getFormatFichier());

            pst.executeUpdate();
            System.out.println("✅ Chapitre ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }
    private static final String API_URL = "https://api-inference.huggingface.co/models/gpt2"; // Vous pouvez changer de modèle ici
    private static final String API_TOKEN = "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"; // Remplacez par votre token API Hugging Face

    public String generateChapterContent(String prompt) {
        try {
            // URL de l'API Hugging Face
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Définir les headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Corps de la requête JSON
            String jsonInputString = "{\"inputs\": \"" + prompt + "\"}";

            // Écrire la requête
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Lire la réponse
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                // La réponse contient la génération du texte
                String generatedText = response.toString();
                return extractGeneratedText(generatedText); // Extrait le texte généré de la réponse
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur lors de la génération de contenu.";
        }
    }

    // Méthode pour extraire le texte généré à partir de la réponse JSON
    private String extractGeneratedText(String response) {
        // Extraction simple du texte généré, selon la structure de la réponse de Hugging Face
        String[] parts = response.split("\"generated_text\":\"");
        if (parts.length > 1) {
            return parts[1].split("\"")[0];
        }
        return "Texte non généré";
    }

    public void exportToPDF(Chapitre chapitre) {
        try {
            // Récupérer le chemin du fichier
            String pdfPath = chapitre.getUrlFichier();

            // Si le chemin est vide ou null, on génère un chemin par défaut
            if (pdfPath == null || pdfPath.trim().isEmpty()) {
                pdfPath = System.getProperty("user.home") + "/Desktop/" + chapitre.getTitre().replaceAll("\\s+", "_") + ".pdf";
            }

            // Vérifier si l'extension .pdf est présente, sinon l'ajouter
            if (!pdfPath.toLowerCase().endsWith(".pdf")) {
                pdfPath += ".pdf";
            }

            // Création du fichier PDF
            File file = new File(pdfPath);
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Ajouter le contenu au PDF
            document.add(new Paragraph("Titre : " + chapitre.getTitre()));
            document.add(new Paragraph("\nContenu : \n" + chapitre.getContenu()));

            // Mettre à jour la base de données avec le chemin et le format du fichier
            chapitre.setUrlFichier(pdfPath);
            chapitre.setFormatFichier("pdf");
            updateChapitreInDatabase(chapitre);

            document.close();
            System.out.println("✅ PDF généré avec succès : " + pdfPath);
        } catch (FileNotFoundException e) {
            System.out.println("❌ Erreur lors de la création du PDF : " + e.getMessage());
        }
    }
    // Ajout de la méthode updateChapitreInDatabase
    public void updateChapitreInDatabase(Chapitre chapitre) {
        String sql = "UPDATE chapitres SET url_fichier = ?, format_fichier = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {  // Utilisation de conn, qui est déjà défini dans la classe
            pstmt.setString(1, chapitre.getUrlFichier());
            pstmt.setString(2, chapitre.getFormatFichier());
            pstmt.setInt(3, chapitre.getId());

            pstmt.executeUpdate();
            System.out.println("✅ Chapitre mis à jour dans la base de données.");

        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour du chapitre : " + e.getMessage());
        }
    }



    @Override
    public void update(Chapitre chapitre) {
        String SQL = "UPDATE chapitres SET id_ressources = ?, titre = ?, url_fichier = ?, contenu = ?, format_fichier = ? WHERE id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(SQL);
            pst.setInt(1, chapitre.getIdRessource());
            pst.setString(2, chapitre.getTitre());
            pst.setString(3, chapitre.getUrlFichier());
            pst.setString(4, chapitre.getContenu());
            pst.setString(5, chapitre.getFormatFichier());
            pst.setInt(6, chapitre.getId());

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Chapitre mis à jour avec succès !");
            } else {
                System.out.println("❌ Aucun chapitre trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @Override
    public void delete(Chapitre chapitre) {
        String SQL = "DELETE FROM chapitres WHERE id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(SQL);
            pst.setInt(1, chapitre.getId());

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Chapitre supprimé avec succès !");
            } else {
                System.out.println("❌ Aucun chapitre trouvé avec cet ID !");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Chapitre> getAll() {
        List<Chapitre> chapitres = new ArrayList<>();
        String SQL = "SELECT * FROM chapitres";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                Chapitre c = new Chapitre();
                c.setId(rs.getInt("id"));
                c.setIdRessource(rs.getInt("id_ressources"));
                c.setTitre(rs.getString("titre"));
                c.setUrlFichier(rs.getString("url_fichier"));
                c.setContenu(rs.getString("contenu"));
                c.setFormatFichier(rs.getString("format_fichier"));

                chapitres.add(c);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération des chapitres : " + e.getMessage());
        }
        return chapitres;
    }

    public Chapitre getById(int id) {
        Chapitre c = null;
        String SQL = "SELECT * FROM chapitres WHERE id = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(SQL);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                c = new Chapitre();
                c.setId(rs.getInt("id"));
                c.setIdRessource(rs.getInt("id_ressources"));
                c.setTitre(rs.getString("titre"));
                c.setUrlFichier(rs.getString("url_fichier"));
                c.setContenu(rs.getString("contenu"));
                c.setFormatFichier(rs.getString("format_fichier"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération du chapitre : " + e.getMessage());
        }
        return c;
    }
}

