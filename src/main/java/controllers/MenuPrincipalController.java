package controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MenuPrincipalController {
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    private StackPane mainContent;
    @FXML
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
    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        dateLabel.setText("📅 " + now.format(dateFormatter));
        timeLabel.setText("🕒 " + now.format(timeFormatter));
    }
    public void ouvrirCulture() {
        chargerVue("AfficherCulture.fxml");
    }

    public void ouvrirParcelle() {
        chargerVue("AfficherParcelle.fxml");
    }

    private void chargerVue(String vue) {
        try {
            Node content = FXMLLoader.load(getClass().getResource("/" + vue));
            mainContent.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
