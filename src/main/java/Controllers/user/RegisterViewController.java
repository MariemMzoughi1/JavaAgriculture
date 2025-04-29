package Controllers.user;

import Entites.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Services.UserService;
import Utils.PasswordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterViewController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label statusLabel;

    @FXML
    private Label loginLabel;

    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleComboBox.getItems().addAll("Admin", "Agriculteur", "Fournisseur", "Client");
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            statusLabel.setText("⚠️ Veuillez remplir tous les champs.");
            return;
        }

        if (userService.getUserByEmail(email) != null) {
            statusLabel.setText("❌ Cet email est déjà utilisé.");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        User newUser = new User(email, hashedPassword, role, username);

        if (userService.ajouterUser(newUser)) {
            statusLabel.setText("✅ Compte créé avec succès !");
            clearFields();
        } else {
            statusLabel.setText("❌ Échec de l'enregistrement.");
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Connexion");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Erreur de chargement de la page de connexion.");
        }
    }

    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
    }
}
