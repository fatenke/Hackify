package services;

import models.Hackathon;
import models.Participation;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailService {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    private static final String USERNAME = "ttttfarah@gmail.com";
    private static final String PASSWORD = "xkfo gwto kfps yiox";

    // Méthode pour créer une session SMTP
    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
    }

    // 📩 Méthode pour envoyer un e-mail simple (texte brut)
    public static void sendPlainTextEmail(String toEmail, String subject, String messageText) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);
            System.out.println("E-mail envoyé avec succès à " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // 📩 Méthode pour envoyer un e-mail en HTML
    public static void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // Ajout du contenu HTML
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(htmlContent, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("E-mail HTML envoyé avec succès à " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void sendEmailWithAttachment(String toEmail, String subject, String messageText, String filePath) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // Partie texte
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(messageText);

            // Partie fichier joint
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(filePath));

            // Ajouter les parties à un e-mail multipart
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("E-mail avec pièce jointe envoyé à " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //les emails

    public static void sendParticipationRequestEmail(Participation participation, String participantEmail) {
        HackathonService hs =new HackathonService();
        Hackathon hackathon= hs.getHackathonById(participation.getIdHackathon());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String subject = "📢 Confirmation de votre demande de participation au Hackathon";
        String content = "Bonjour,\n\n"
                + "Nous avons bien reçu votre demande de participation au hackathon " + hackathon.getNom_hackathon() + " qui aura lieu du " + hackathon.getDate_debut().format(formatter)+ " au " + hackathon.getDate_fin().format(formatter) + " à " + hackathon.getLieu() + ".\n"
                + "Votre demande est en cours de traitement et nous vous tiendrons informé(e) de l’état de votre inscription.\n\n"
                + "Cordialement,\nL'équipe d'organisation de " + hackathon.getNom_hackathon();
        sendPlainTextEmail(participantEmail, subject, content);
    }

    public static void sendParticipationAcceptanceEmail(Participation participation, String participantEmail) {
        HackathonService hs = new HackathonService();
        Hackathon hackathon = hs.getHackathonById(participation.getIdHackathon());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String subject = "🎉 Votre participation au Hackathon a été acceptée ! 🚀";
        String content = "Bonjour 👋,\n\n"
                + "Nous avons le plaisir de vous annoncer que votre demande de participation au hackathon "
                + hackathon.getNom_hackathon() + " a été **acceptée** ✅ ! Ce hackathon se déroulera du "
                + hackathon.getDate_debut().format(formatter) + " au " + hackathon.getDate_fin().format(formatter)
                + " à " + hackathon.getLieu() + ".\n\n"
                + "✨ Nous sommes impatients de vous accueillir et de découvrir vos idées brillantes ! 🚀\n\n"
                + "Si vous avez des questions ou besoin de plus d'informations, n'hésitez pas à nous contacter ! 📧\n\n"
                + "Cordialement,\nL'équipe d'organisation de " + hackathon.getNom_hackathon() + " 💡";
        sendPlainTextEmail(participantEmail, subject, content);
    }

    public static void sendParticipationRejectionEmail(Participation participation, String participantEmail) {
        HackathonService hs = new HackathonService();
        Hackathon hackathon = hs.getHackathonById(participation.getIdHackathon());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String subject = "🚫 Votre demande de participation au Hackathon a été rejetée";
        String content = "Bonjour 👋,\n\n"
                + "Nous sommes désolés de vous informer que votre demande de participation au hackathon "
                + hackathon.getNom_hackathon() + " n'a malheureusement pas été retenue ❌. Ce hackathon se déroulera du "
                + hackathon.getDate_debut().format(formatter) + " au " + hackathon.getDate_fin().format(formatter)
                + " à " + hackathon.getLieu() + ".\n\n"
                + "Nous vous remercions pour votre intérêt 🙏 et espérons que vous participerez à un futur hackathon ! 🌱\n\n"
                + "Cordialement,\nL'équipe d'organisation de " + hackathon.getNom_hackathon() + " 💡";
        sendPlainTextEmail(participantEmail, subject, content);
    }
    public static void sendHackathonFullEmail(Participation participation, String participantEmail) {
        HackathonService hs = new HackathonService();
        Hackathon hackathon = hs.getHackathonById(participation.getIdHackathon());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String subject = "⚠️ Le Hackathon est complet 🛑";
        String content = "Bonjour 👋,\n\n"
                + "Nous vous informons que malheureusement, le hackathon " + hackathon.getNom_hackathon()
                + " prévu du " + hackathon.getDate_debut().format(formatter) + " au "
                + hackathon.getDate_fin().format(formatter) + " à " + hackathon.getLieu()
                + " est désormais complet. 🚫\n\n"
                + "Nous avons été ravis de l'énorme intérêt porté à cet événement et nous espérons vous offrir d'autres opportunités très bientôt ! 💡\n\n"
                + "Restez connecté(e) pour plus d'événements à venir ✨.\n\n"
                + "Cordialement,\nL'équipe d'organisation de " + hackathon.getNom_hackathon() + " 💻";
        sendPlainTextEmail(participantEmail, subject, content);
    }

}


