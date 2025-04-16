package Controllers;

import Entites.User;
import Services.UserService;
import Utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginViewController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label registerLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠️ Email et mot de passe sont requis.");
            return;
        }

        User user = userService.getUserByEmail(email);
        if (user != null) {
            String hashedInput = PasswordUtils.hashPassword(password);
            if (user.getPassword().equals(hashedInput)) {
                // Authentification réussie
                statusLabel.setText("✅ Connexion réussie !");
                redirectUser(user);
                return;
            }
        }

        statusLabel.setText("❌ Email ou mot de passe incorrect.");
    }

    private void redirectUser(User user) {
        try {
            String role = user.getRole().toUpperCase();
            String fxmlPath;

            if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
                fxmlPath = "/admin-dashboard-view.fxml";
            } else {
                fxmlPath = "/machine-home.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 800, 600);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Impossible de charger le tableau de bord.");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Inscription");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Impossible de charger la page d'inscription.");
        }
    }
}
