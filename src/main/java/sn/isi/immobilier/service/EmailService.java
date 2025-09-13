package sn.isi.immobilier.service;

// Importations Jakarta Mail corrigées
import jakarta.mail.Session;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.Multipart;

// Importations Jakarta Activation
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;

// Import pour ByteArrayDataSource
import jakarta.mail.util.ByteArrayDataSource;

// Import de votre utilitaire
import sn.isi.immobilier.util.ConfigUtil;

// Imports Java standard
import java.io.File;
import java.util.Properties;

public class EmailService {

    private final String username = ConfigUtil.get("mail.username");
    private final String password = ConfigUtil.get("mail.password");

    // Envoi simple sans pièce jointe
    public void sendEmail(String to, String subject, String messageContent) throws MessagingException {
        Session session = createSession();
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(messageContent);
        Transport.send(message);
    }

    // Envoi avec pièce jointe à partir d'un File
    public void sendEmailWithAttachment(String to, String subject, String messageContent, File attachment) throws MessagingException {
        Session session = createSession();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        // Corps du mail
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(messageContent);

        // Pièce jointe
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new FileDataSource(attachment);
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(attachment.getName());

        // Combinaison texte + pièce jointe
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);
        Transport.send(message);
    }

    // Envoi avec pièce jointe à partir d'un byte[]
    public void sendEmailWithAttachment(String to, String subject, String messageContent, byte[] attachmentBytes, String fileName) throws MessagingException {
        Session session = createSession();

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        // Corps du mail
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(messageContent);

        // Pièce jointe à partir des bytes
        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(attachmentBytes, "application/pdf");
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(fileName);

        // Combinaison texte + pièce jointe
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);
        System.out.println("=== Envoi de mail ===");
        System.out.println("À: " + to);
        System.out.println("Sujet: " + subject);
        System.out.println("Corps: " + messageContent);
        System.out.println("Pièce jointe: " + fileName);

        try {
            Transport.send(message);
            System.out.println("Mail envoyé avec succès !");
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi du mail : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

    }

    // Création de la session SMTP
    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true"); // Authentification obligatoire
        props.put("mail.smtp.starttls.enable", "true"); // TLS activé
        props.put("mail.smtp.host", ConfigUtil.get("mail.smtp.host")); // ex: smtp.gmail.com
        props.put("mail.smtp.port", ConfigUtil.get("mail.smtp.port")); // ex: 587
        props.put("mail.smtp.ssl.trust", ConfigUtil.get("mail.smtp.host")); // évite les erreurs de certificat
        System.out.println("=== SMTP CONFIGURATION ===");
        props.forEach((k,v) -> System.out.println(k + " = " + v));
        System.out.println("==========================");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(username, password);
            }
        });
    }
}
