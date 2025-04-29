package com.esprit.Controllers;

import com.esprit.models.Parcelle;
import com.esprit.services.ParcelleService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.awt.*;
import java.awt.TrayIcon.MessageType;

import java.util.List;

public class AlertesParcellesController {

    @FXML
    private VBox alertesVBox;

    private final ParcelleService parcelleService = new ParcelleService();

    @FXML
    public void initialize() {

        List<Parcelle> alertes = parcelleService.getParcellesEnFinDeLocation();

        if (alertes.isEmpty()) {
            Label noAlert = new Label("✅ Aucune parcelle en fin de location.");
            noAlert.setStyle("-fx-font-size: 16px; -fx-text-fill: green;");
            alertesVBox.getChildren().add(noAlert);
        } else {
            for (Parcelle p : alertes) {
                VBox carte = new VBox();
                carte.setSpacing(5);
                carte.setStyle(
                        "-fx-background-color: #ffe6e6;" +
                                "-fx-border-color: #e74c3c;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 12;" +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                );

                Label titre = new Label("⚠️ Parcelle #" + p.getId() + " - Zone : " + p.getZone());
                titre.setStyle("-fx-font-size: 16px; -fx-text-fill: #c0392b; -fx-font-weight: bold;");

                Label finLoc = new Label("📅 Fin de location : " + p.getDateDeFinLocation());
                finLoc.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");

                Label sol = new Label("🧱 Type de sol : " + p.getTypeSol() + " | État : " + p.getEtat());
                sol.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

                carte.getChildren().addAll(titre, finLoc, sol);
                alertesVBox.getChildren().add(carte);
                afficherNotification(
                        "Parcelle en fin de location",
                        "Parcelle #" + p.getId() + " (" + p.getZone() + ") - Fin : " + p.getDateDeFinLocation()
                );
            }
        }
    }
    private void afficherNotification(String titre, String message) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("");// tu peux mettre une icône ici

            TrayIcon trayIcon = new TrayIcon(image, "Alerte Parcelle");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Alerte de votre application");

            try {
                tray.add(trayIcon);
                trayIcon.displayMessage(titre, message, MessageType.WARNING);
            } catch (AWTException e) {
                System.err.println("Erreur de notification : " + e.getMessage());
            }
        } else {
            System.err.println("SystemTray non supporté");
        }
    }
}
