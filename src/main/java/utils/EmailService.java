package utils;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


import java.util.Properties;


public class EmailService {

    // ✅ Replace with your Gmail & App Password
    private static final String FROM_EMAIL = "lamloumamine7@gmail.com";
    private static final String FROM_PASSWORD = "iopv leow ljoj ndoo"; // your Gmail App Password

    public static boolean sendResetToken(String toEmail, String resetToken) {
        String subject = "🔐 Réinitialisation de votre mot de passe - DevHarvest";
        String body = "Bonjour,\n\n"
                + "Voici votre code de réinitialisation de mot de passe :\n\n"
                + "👉 Code : " + resetToken + "\n\n"
                + "Veuillez entrer ce code dans l'application pour réinitialiser votre mot de passe.\n\n"
                + "Si vous n'avez pas demandé ce changement, ignorez ce message.\n\n"
                + "Cordialement,\nL'équipe DevHarvest 🌾";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✅ Email envoyé à " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("❌ Erreur d'envoi de l'email : " + e.getMessage());
            return false;
        }
    }
}
