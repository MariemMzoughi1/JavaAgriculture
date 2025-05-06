package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UserService;
import utils.PasswordUtils;

public class ResetPasswordController {

    @FXML private TextField emailField;
    @FXML private TextField tokenField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText().trim();
        String token = tokenField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 🔍 Validation
        if (email.isEmpty() || token.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("❌ Tous les champs sont obligatoires.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("❌ Les mots de passe ne correspondent pas.");
            return;
        }

        // 🔐 Vérifier le token
        boolean isValid = userService.verifyResetToken(email, token);

        if (!isValid) {
            messageLabel.setText("❌ Code de vérification invalide ou expiré.");
            return;
        }

        // ✅ Mettre à jour le mot de passe
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        boolean updated = userService.updatePassword(email, hashedPassword);

        if (updated) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("✅ Mot de passe réinitialisé avec succès. Redirection...");

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/login-view.fxml"));
                            Scene scene = new Scene(loader.load(), 800, 600);
                            Stage stage = (Stage) emailField.getScene().getWindow();
                            stage.setScene(scene);
                            stage.setTitle("Connexion");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (InterruptedException ignored) {}
            }).start();
        } else {
            messageLabel.setText("❌ Échec de la mise à jour du mot de passe.");
        }
    }

    public void prefillEmail(String email) {
        emailField.setText(email);
    }
}
