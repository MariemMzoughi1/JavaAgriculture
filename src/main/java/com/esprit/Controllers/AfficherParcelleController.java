package com.esprit.Controllers;

import com.esprit.models.Parcelle;
import com.esprit.services.ParcelleService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AfficherParcelleController {

    @FXML
    private TextField searchField;

    @FXML
    private VBox cardContainer;

    private final ParcelleService parcelleService = new ParcelleService();

    @FXML
    public void initialize() {
        refreshCards();
    }

    @FXML
    private void refreshCards() {
        afficherParcelles(parcelleService.afficher());
    }

    private HBox createParcelleCard(Parcelle parcelle) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: white; " +
                "-fx-padding: 20; " +
                "-fx-border-radius: 12; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        card.setPrefWidth(950);
        card.setPrefHeight(220);

        // 📷 Image
        VBox left = new VBox();
        ImageView imageView = new ImageView();
        try {
            String imageUrl = parcelle.getImage();
            if (imageUrl == null || imageUrl.isBlank())
                throw new IllegalArgumentException("Image vide");
            imageView.setImage(new Image(imageUrl, 260, 160, false, false));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResource("/images/default-image.jpg").toExternalForm(), 260, 160, false, false));
        }
        imageView.setStyle("-fx-border-radius: 12; -fx-background-radius: 12;");
        left.getChildren().add(imageView);

        // 📋 Informations
        VBox infoBox = new VBox(10);
        infoBox.setPrefWidth(600);

        Label titre = new Label("📍 Parcelle - Zone : " + parcelle.getZone());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label desc = new Label("📝 " + parcelle.getDescription());
        Label surface = new Label("📏 Superficie : " + parcelle.getSuperficie() + " m²");
        Label prix = new Label("💰 Prix : " + parcelle.getPrixDeLocation() + " DT");
        Label debut = new Label("📅 Début : " + parcelle.getDateDeLocation());
        Label fin = new Label("📅 Fin : " + parcelle.getDateDeFinLocation());

        Label sol = new Label("🌱 Sol : " + parcelle.getTypeSol());
        Label etat = new Label("✔ État : " + parcelle.getEtat());

        Label alerte = new Label();
        long joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), parcelle.getDateDeFinLocation());
        if (joursRestants <= 7) {
            alerte.setText("⚠️ Fin proche !");
            alerte.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }

        infoBox.getChildren().addAll(titre, desc, surface, prix, debut, fin, sol, etat, alerte);

        // 🛠️ Boutons
        Button btnVoir = new Button("👁 Voir");
        Button btnModifier = new Button("✏️ Modifier");
        Button btnSupprimer = new Button("🗑 Supprimer");


        btnModifier.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 15;");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 15;");
        btnVoir.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 15;");

        btnModifier.setOnAction(event -> ouvrirModifierParcelle(parcelle));
        btnSupprimer.setOnAction(event -> supprimerParcelle(parcelle));
        btnVoir.setOnAction(event -> afficherDetailsParcelle(parcelle));

        HBox buttonsBox = new HBox(10, btnModifier, btnSupprimer, btnVoir);
        buttonsBox.setStyle("-fx-padding: 10 0 0 0;");
        infoBox.getChildren().add(buttonsBox);

        card.getChildren().addAll(left, infoBox);
        return card;
    }

    private void afficherParcelles(List<Parcelle> parcelles) {
        cardContainer.getChildren().clear();
        if (parcelles.isEmpty()) {
            Label emptyLabel = new Label("🌾 Aucune parcelle trouvée.");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d;");
            cardContainer.getChildren().add(emptyLabel);
        } else {
            for (Parcelle parcelle : parcelles) {
                cardContainer.getChildren().add(createParcelleCard(parcelle));
            }
        }
    }

    private void ouvrirModifierParcelle(Parcelle parcelle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierParcelle.fxml"));
            Parent root = loader.load();
            ModifierParcelleController controller = loader.getController();
            controller.setParcelle(parcelle);
            controller.setOnModificationSuccess(this::refreshCards);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Parcelle");
            stage.showAndWait();
            refreshCards();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerParcelle(Parcelle parcelle) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Suppression de la Parcelle");
        confirm.setHeaderText("🗑 Voulez-vous vraiment supprimer cette parcelle ?");
        confirm.setContentText("Zone : " + parcelle.getZone() + "\nSuperficie : " + parcelle.getSuperficie() + " m²");

        // 🎨 Styliser l'alerte
        DialogPane dialogPane = confirm.getDialogPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #f8f9fa);" +
                "-fx-border-color: #d32f2f;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 20;");

        // Styliser le titre
        Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 20px; -fx-font-weight: bold;");
        }

        // Styliser le texte
        Label contentLabel = (Label) dialogPane.lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #37474f;");
        }

        // Définir des boutons personnalisés
        ButtonType confirmButton = new ButtonType("✅ Oui, Supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("❌ Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirm.getButtonTypes().setAll(confirmButton, cancelButton);

        confirm.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                parcelleService.supprimer(parcelle.getId());
                refreshCards();

                // 🔔 Petit message de succès
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Suppression réussie");
                success.setHeaderText(null);
                success.setContentText("✅ Parcelle supprimée avec succès !");
                DialogPane successPane = success.getDialogPane();
                successPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #e8f5e9, #ffffff);" +
                        "-fx-border-color: #43a047;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;");
                successPane.lookup(".content.label").setStyle("-fx-font-size: 16px; -fx-text-fill: #2e7d32;");
                success.show();
            }
        });
    }



    private void afficherDetailsParcelle(Parcelle parcelle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VoirParcelle.fxml"));
            Parent root = loader.load();
            VoirParcelleController controller = loader.getController();
            controller.setParcelle(parcelle);

            Stage stage = new Stage();
            stage.setTitle("Détails Parcelle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterParcelle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterParcelle.fxml"));
            Parent root = loader.load();
            AjouterParcelleController controller = loader.getController();
            controller.setOnAjoutSuccess(this::refreshCards);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Parcelle");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rechercherParEtat(javafx.scene.input.KeyEvent keyEvent) {
        String etatRecherche = searchField.getText().trim().toLowerCase();
        if (etatRecherche.isEmpty()) {
            afficherParcelles(parcelleService.afficher());
        } else {
            List<Parcelle> resultatFiltre = parcelleService.afficher()
                    .stream()
                    .filter(p -> p.getEtat().toLowerCase().contains(etatRecherche))
                    .toList();
            afficherParcelles(resultatFiltre);
        }
    }
}
