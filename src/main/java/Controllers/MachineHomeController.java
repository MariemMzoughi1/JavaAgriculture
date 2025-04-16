package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Services.Session;

import java.io.IOException;

public class MachineHomeController {

    @FXML
    private void handleAddMachine(ActionEvent event) throws IOException {
        loadPage(event, "/machine-add.fxml");
    }

    @FXML
    private void handleListMachines(ActionEvent event) throws IOException {
        loadPage(event, "/machine-index.fxml");
    }

    @FXML
    private void handleReservation(ActionEvent event) throws IOException {
        loadPage(event, "/reservation-view.fxml");
    }

    @FXML
    private void handleForum(ActionEvent event) throws IOException {
        loadPage(event, "/ListeForum.fxml");
    }

    @FXML
    private void handleProduit(ActionEvent event) throws IOException {
        loadPage(event, "/AfficherProduit.fxml");
    }

    @FXML
    private void handleCulture(ActionEvent event) throws IOException {
        loadPage(event, "/AfficherCulture.fxml");
    }

    @FXML
    private void handleParcelle(ActionEvent event) throws IOException {
        loadPage(event, "/AfficherParcelle.fxml");
    }




    private void loadPage(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("🌿 DevHarvest - Gestion");
        stage.show();
    }

    @FXML
    private void handleLogout(ActionEvent event){
        Session.clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

