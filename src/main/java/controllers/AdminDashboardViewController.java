package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import services.Session;

import java.io.IOException;

public class AdminDashboardViewController {

    @FXML
    private void handleManageUsers(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/user-list-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            // Get stage from the event's source node
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Manage Users");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleManageMachines(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/machine-table-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Machines");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleManageReservations(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/reservation-table-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event){
        Session.clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleManagePosts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/admin-posts-view.fxml"));
            VBox root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion des posts");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de l'ouverture de l'interface de gestion des posts : " + e.getMessage());
        }
    }

}
