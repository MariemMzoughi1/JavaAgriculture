package controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.UserService;
import utils.PasswordUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterViewController implements Initializable {

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
        roleComboBox.getItems().addAll( "Agriculteur", "Fournisseur", "Client");
    }

    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        if (email.isEmpty() || password.isEmpty() || role == null) {
            statusLabel.setText("⚠️ Please fill in all fields.");
            return;
        }

        // Check if email already exists
        if (userService.getUserByEmail(email) != null) {
            statusLabel.setText("❌ Email already registered. Please use a different email.");
            return;
        }
        User newUser = new User(email, password, role, null);

        String hashedPassword = PasswordUtils.hashPassword(newUser.getPassword());
        newUser.setPassword(hashedPassword);

        if (userService.addUser(newUser)) {
            statusLabel.setText("✅ Registration successful!");
            clearFields();
        } else {
            statusLabel.setText("❌ Registration failed. Please try again.");
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/login-view.fxml"));
            Scene scene = new Scene(loader.load(),800,600);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Failed to load login page.");
        }
    }

    private void clearFields() {
        emailField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
    }
}
