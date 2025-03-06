package services;

import java.io.File;
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
}


