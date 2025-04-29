package com.esprit.Controllers;

import com.esprit.models.Parcelle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VoirParcelleController {

    @FXML
    private VBox infoBox;

    private Parcelle parcelle;

    public void setParcelle(Parcelle parcelle) {
        this.parcelle = parcelle;
        afficherInfos();
    }

    private void afficherInfos() {
        infoBox.getChildren().clear();

        Label zone = new Label("📍 Zone : " + parcelle.getZone());
        Label description = new Label("📝 Description : " + parcelle.getDescription());
        Label superficie = new Label("📐 Superficie : " + parcelle.getSuperficie() + " m²");
        Label prix = new Label("💰 Prix de location : " + parcelle.getPrixDeLocation() + " DT");
        Label debut = new Label("📅 Date de début : " + parcelle.getDateDeLocation());
        Label fin = new Label("📅 Date de fin : " + parcelle.getDateDeFinLocation());
        Label typeSol = new Label("🧱 Type de sol : " + parcelle.getTypeSol());
        Label etat = new Label("✅ État : " + parcelle.getEtat());

        for (Label label : new Label[]{zone, description, superficie, prix, debut, fin, typeSol, etat}) {
            label.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
            infoBox.getChildren().add(label);
        }
    }

    @FXML
    private void fermer() {
        Stage stage = (Stage) infoBox.getScene().getWindow();
        stage.close();
    }
}
