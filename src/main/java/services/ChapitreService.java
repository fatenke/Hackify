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
            PreparedStatement pst = this.conn.prepareStatement(SQL);
            pst.setInt(1, chapitre.getIdRessource());
            pst.setString(2, chapitre.getTitre());
            pst.setString(3, chapitre.getUrlFichier());
            pst.setString(4, chapitre.getContenu());
            pst.setString(5, chapitre.getFormatFichier());
            pst.executeUpdate();
            System.out.println("✅ Chapitre ajouté avec succès !");
        } catch (SQLException var4) {
            SQLException e = var4;
            System.out.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }

    }

    public String generateChapterContent(String prompt) {
        try {
            URL url = new URL("https://api-inference.huggingface.co/models/gpt2");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer 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");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String jsonInputString = "{\"inputs\": \"" + prompt + "\"}";
            OutputStream os = connection.getOutputStream();

            try {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            } catch (Throwable var12) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                    }
                }

                throw var12;
            }

            if (os != null) {
                os.close();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

            String var9;
            try {
                StringBuilder response = new StringBuilder();

                while(true) {
                    String responseLine;
                    if ((responseLine = br.readLine()) == null) {
                        String generatedText = response.toString();
                        var9 = this.extractGeneratedText(generatedText);
                        break;
                    }

                    response.append(responseLine.trim());
                }
            } catch (Throwable var13) {
                try {
                    br.close();
                } catch (Throwable var10) {
                    var13.addSuppressed(var10);
                }

                throw var13;
            }

            br.close();
            return var9;
        } catch (IOException var14) {
            IOException e = var14;
            e.printStackTrace();
            return "Erreur lors de la génération de contenu.";
        }
    }

    private String extractGeneratedText(String response) {
        String[] parts = response.split("\"generated_text\":\"");
        return parts.length > 1 ? parts[1].split("\"")[0] : "Texte non généré";
    }

    public void exportToPDF(Chapitre chapitre) {
        try {
            String pdfPath = chapitre.getUrlFichier();
            if (pdfPath == null || pdfPath.trim().isEmpty()) {
                String var10000 = System.getProperty("user.home");
                pdfPath = var10000 + "/Desktop/" + chapitre.getTitre().replaceAll("\\s+", "_") + ".pdf";
            }

            if (!pdfPath.toLowerCase().endsWith(".pdf")) {
                pdfPath = pdfPath + ".pdf";
            }

            File file = new File(pdfPath);
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.add(new Paragraph("Titre : " + chapitre.getTitre()));
            document.add(new Paragraph("\nContenu : \n" + chapitre.getContenu()));
            chapitre.setUrlFichier(pdfPath);
            chapitre.setFormatFichier("pdf");
            this.updateChapitreInDatabase(chapitre);
            document.close();
            System.out.println("✅ PDF généré avec succès : " + pdfPath);
        } catch (FileNotFoundException var7) {
            FileNotFoundException e = var7;
            System.out.println("❌ Erreur lors de la création du PDF : " + e.getMessage());
        }

    }

    public void updateChapitreInDatabase(Chapitre chapitre) {
        String sql = "UPDATE chapitres SET url_fichier = ?, format_fichier = ? WHERE id = ?";

        try {
            PreparedStatement pstmt = this.conn.prepareStatement(sql);

            try {
                pstmt.setString(1, chapitre.getUrlFichier());
                pstmt.setString(2, chapitre.getFormatFichier());
                pstmt.setInt(3, chapitre.getId());
                pstmt.executeUpdate();
                System.out.println("✅ Chapitre mis à jour dans la base de données.");
            } catch (Throwable var7) {
                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                    }
                }

                throw var7;
            }

            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException var8) {
            SQLException e = var8;
            System.out.println("❌ Erreur lors de la mise à jour du chapitre : " + e.getMessage());
        }

    }
    @Override

    public void modifier(Chapitre chapitre) {
        String SQL = "UPDATE chapitres SET id_ressources = ?, titre = ?, url_fichier = ?, contenu = ?, format_fichier = ? WHERE id = ?";

        try {
            PreparedStatement pst = this.conn.prepareStatement(SQL);
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
        } catch (SQLException var5) {
            SQLException e = var5;
            System.out.println("❌ Erreur lors de la mise à jour : " + e.getMessage());
        }

    }
    @Override

    public void supprimer(int id) throws SQLException {
    }

    public void supprimer(Chapitre chapitre) {
        String SQL = "DELETE FROM chapitres WHERE id = ?";

        try {
            PreparedStatement pst = this.conn.prepareStatement(SQL);
            pst.setInt(1, chapitre.getId());
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Chapitre supprimé avec succès !");
            } else {
                System.out.println("❌ Aucun chapitre trouvé avec cet ID !");
            }
        } catch (SQLException var5) {
            SQLException e = var5;
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }

    }
    @Override

    public List<Chapitre> recuperer() {
        List<Chapitre> chapitres = new ArrayList();
        String SQL = "SELECT * FROM chapitres";

        try {
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while(rs.next()) {
                Chapitre c = new Chapitre();
                c.setId(rs.getInt("id"));
                c.setIdRessource(rs.getInt("id_ressources"));
                c.setTitre(rs.getString("titre"));
                c.setUrlFichier(rs.getString("url_fichier"));
                c.setContenu(rs.getString("contenu"));
                c.setFormatFichier(rs.getString("format_fichier"));
                chapitres.add(c);
            }
        } catch (SQLException var6) {
            SQLException e = var6;
            System.out.println("❌ Erreur lors de la récupération des chapitres : " + e.getMessage());
        }

        return chapitres;
    }

    public Chapitre getById(int id) {
        Chapitre c = null;
        String SQL = "SELECT * FROM chapitres WHERE id = ?";

        try {
            PreparedStatement pst = this.conn.prepareStatement(SQL);
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
        } catch (SQLException var6) {
            SQLException e = var6;
            System.out.println("❌ Erreur lors de la récupération du chapitre : " + e.getMessage());
        }

        return c;
    }
}

