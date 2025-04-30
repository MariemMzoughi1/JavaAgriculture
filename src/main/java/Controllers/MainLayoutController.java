package Controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainLayoutController {

    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    private StackPane contentArea;
    public void initialize() {
        updateDateTime(); // première mise à jour immédiate

        // Mise à jour toutes les secondes
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> updateDateTime()),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void loadContent(String fxmlPath) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAccueil(ActionEvent event) {
        loadContent("/Sidebar.fxml"); // ou une autre page d’accueil
    }

    public void handleMachines(ActionEvent event) {
        loadContent("/machine-index.fxml");
    }

    public void handleReservation(ActionEvent event) {
        loadContent("/reservation-view.fxml");
    }

    public void handleForum(ActionEvent event) {
        loadContent("/ListeForum.fxml");
    }

    public void handleProduit(ActionEvent event) {
        loadContent("/AfficherProduit.fxml");
    }

    public void handleCulture(ActionEvent event) {
        loadContent("/AfficherCulture.fxml");
    }

    public void handleParcelle(ActionEvent event) {
        loadContent("/AfficherParcelle.fxml");
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

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        dateLabel.setText("📅 " + now.format(dateFormatter));
        timeLabel.setText("🕒 " + now.format(timeFormatter));
    }
}
