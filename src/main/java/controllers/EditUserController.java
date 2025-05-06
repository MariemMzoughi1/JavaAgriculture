package controllers;

import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.UserService;

public class EditUserController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;


    private final UserService userService = new UserService();
    private User userToEdit;



    public void setUser(User user) {
        this.userToEdit = user;
        if (user != null) {
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());

        }
    }

    @FXML
    private void handleSaveUser() {
        if (userToEdit != null) {
            userToEdit.setEmail(emailField.getText());
            userToEdit.setPassword(passwordField.getText());

            userService.updateUser(userToEdit);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.close();
        }
    }
}
