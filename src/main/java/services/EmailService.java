package services;
import jakarta.mail.Session;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    public static void sendConfirmationEmail(String toEmail) {
        String fromEmail = "eyouta4@gmail.com";  // Ton adresse email
        String password = "vxgc bmph wsnu bgea";  // Ton mot de passe ou mot de passe d'application

        // Paramètres SMTP de Gmail
        String host = "smtp.gmail.com";
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");

        // Créer une session avec authentification
        jakarta.mail.Session session = jakarta.mail.Session.getInstance(properties, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Créer un message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Confirmation de Commande");
            message.setText("Bonjour,\n\nVotre commande a été enregistrée avec succès. Merci de votre confiance.\n vous recevrez votre commande dans 1-2 jours.\n\nCordialement,\nResponsable des ventes\nEya Rhouma ");

            // Envoyer l'email
            Transport.send(message);
            System.out.println("Email envoyé avec succès !");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
