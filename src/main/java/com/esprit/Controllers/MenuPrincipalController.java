package com.esprit.Controllers;

import com.esprit.models.Parcelle;
import com.esprit.services.ParcelleService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MenuPrincipalController {

    @FXML
    private Label dateLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private StackPane mainContent;

    @FXML
    private VBox sidebar;

    @FXML
    private Button toggleSidebarButton;

    @FXML
    private Label sidebarTitle;

    private boolean sidebarOpen = true;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public void initialize() {
        updateDateTime();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> updateDateTime()),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        verifierAlertesParcelles();
    }

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        dateLabel.setText("📅 " + now.format(dateFormatter));
        timeLabel.setText("🕒 " + now.format(timeFormatter));
    }

    @FXML
    private void toggleSidebar() {
        if (sidebarOpen) {
            sidebar.setPrefWidth(60); // Fermer
            sidebarTitle.setVisible(false);

            for (Node node : sidebar.getChildren()) {
                if (node instanceof Button button && button != toggleSidebarButton) {
                    button.setText(""); // Cacher texte
                    button.setPrefWidth(40);
                }
            }
            sidebarOpen = false;
        } else {
            sidebar.setPrefWidth(200); // Ouvrir
            sidebarTitle.setVisible(true);

            int i = 0;
            for (Node node : sidebar.getChildren()) {
                if (node instanceof Button button && button != toggleSidebarButton) {
                    switch (i) {
                        case 1 -> button.setText("Culture");
                        case 2 -> button.setText("Parcelle");
                        case 3 -> button.setText("📍 Alertes");
                        case 4 -> button.setText("🔮 Prévisions");
                        case 5 -> button.setText("🧠 Suggestion");
                        case 6 -> button.setText("📊 Statistiques");
                        case 7 -> button.setText("☀️ Météo");
                    }
                    button.setPrefWidth(180);
                    i++;
                }
            }
            sidebarOpen = true;
        }
    }

    public void ouvrirCulture() {
        chargerVue("AfficherCulture.fxml");
    }

    public void ouvrirParcelle() {
        chargerVue("AfficherParcelle.fxml");
    }

    @FXML
    public void ouvrirAlertesParcelles() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AlertesParcelles.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("📍 Alertes de fin de location");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirPrevisionRecolte() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PrevisionRecolte.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Prévision de Récolte");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirSuggestionCulture() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SuggestionCulture.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Suggestion de Culture");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirStatistiquesQuantites() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardQuantite.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("📊 Statistiques des Quantités par Catégorie");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirMeteo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MeteoView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("📈 Météo Tunis");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chargerVue(String vue) {
        try {
            Node content = FXMLLoader.load(getClass().getResource("/" + vue));
            mainContent.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void afficherNotification(String titre, String message) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("");
            TrayIcon trayIcon = new TrayIcon(image, "Alerte Parcelle");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Alerte de votre application");

            try {
                tray.add(trayIcon);
                trayIcon.displayMessage(titre, message, TrayIcon.MessageType.WARNING);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("SystemTray non supporté");
        }
    }

    private void verifierAlertesParcelles() {
        ParcelleService parcelleService = new ParcelleService();
        List<Parcelle> alertes = parcelleService.getParcellesEnFinDeLocation();

        if (!alertes.isEmpty()) {
            for (Parcelle p : alertes) {
                afficherNotification(
                        "⚠️ Parcelle #" + p.getId(),
                        "Fin de location prévue le " + p.getDateDeFinLocation()
                );
            }
        }
    }
}
