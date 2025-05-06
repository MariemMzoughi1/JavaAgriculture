package controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UserService;
import utils.EmailService;

import java.io.IOException;
import java.util.UUID;

public class SendResetTokenController {

    @FXML private TextField emailField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleSendToken() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            messageLabel.setText("❌ Email est requis.");
            return;
        }

        if (userService.getUserByEmail(email) == null) {
            messageLabel.setText("❌ Aucun utilisateur trouvé avec cet email.");
            return;
        }

        // 🔐 Générer un token aléatoire
        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 🔃 Enregistrer le token dans la base
        User user = userService.getUserByEmail(email);
        user.setReset_token(token);
        userService.editUser(user);

        // 📧 Envoyer l'email
        boolean sent = EmailService.sendResetToken(email, token);
        if (sent) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("✅ Code envoyé. Redirection...");

            // ⏳ Redirection après 2 secondes
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> redirectToReset(email));
                } catch (InterruptedException ignored) {}
            }).start();
        } else {
            messageLabel.setText("❌ Erreur lors de l'envoi du mail.");
        }
    }

    private void redirectToReset(String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/reset-password-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Réinitialisation du mot de passe");

            // Préremplir l'email (optionnel)
            ResetPasswordController controller = loader.getController();
            controller.prefillEmail(email);

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("❌ Erreur de redirection.");
        }
    }
}
