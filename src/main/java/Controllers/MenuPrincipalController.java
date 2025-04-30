package Controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MenuPrincipalController {
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;

    @FXML
    private StackPane mainContent;

    @FXML
    public void initialize() {
        ;
    }

    public void ouvrirCulture() {
        chargerVue("/AfficherCulture.fxml");
    }

    public void ouvrirParcelle() {
        chargerVue("/AfficherParcelle.fxml");
    }
    @FXML
    private void handleAddMachine(ActionEvent event) throws IOException {
        chargerVue("/machine-add.fxml");
    }



    public void handleMachines(ActionEvent event) {
        chargerVue("/machine-index.fxml");
    }

    public void handleReservation(ActionEvent event) {
        chargerVue("/reservation-view.fxml");
    }

    public void handleForum(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeForum.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleProduit(ActionEvent event) {
        chargerVue("/AfficherProduit.fxml");
    }



    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void chargerVue(String vue) {
        try {
            Node content = FXMLLoader.load(getClass().getResource(vue)); // PAS de slash ici
            mainContent.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
