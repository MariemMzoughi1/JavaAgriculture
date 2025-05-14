package controllers;

import entities.Parcelle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ParcelleService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AfficherParcelleController {

    @FXML private TextField searchField;
    @FXML private VBox cardContainer;

    private final ParcelleService parcelleService = new ParcelleService();

    @FXML
    public void initialize() {
        refreshCards();
    }

    @FXML
    private void refreshCards() {
        afficherParcelles(parcelleService.afficher());
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

    private HBox createParcelleCard(Parcelle parcelle) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 2);");
        card.setPrefWidth(950);
        card.setPrefHeight(220);

        VBox left = new VBox();
        ImageView imageView = new ImageView();
        try {
            String imageName = parcelle.getImage();
            if (imageName == null || imageName.isBlank())
                throw new IllegalArgumentException("Image vide");

            String imagePath = "file:/C:/Users/DAMIANO/pidevvvvvvvvv/DevHarvest-forum/public/uploads/parcelles/" + imageName;
            imageView.setImage(new Image(imagePath, 260, 160, false, false));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResource("/images/default-image.jpg").toExternalForm(), 260, 160, false, false));
        }
        imageView.setStyle("-fx-border-radius: 12; -fx-background-radius: 12;");
        left.getChildren().add(imageView);

        VBox infoBox = new VBox(10);
        infoBox.setPrefWidth(600);

        Label titre = new Label("📍 Parcelle - Zone : " + parcelle.getZone());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill:#2c3e50;");
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

        Button btnVoir = new Button("👁 Voir");
        Button btnModifier = new Button("✏️ Modifier");
        Button btnSupprimer = new Button("🗑 Supprimer");

        btnVoir.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 15;");
        btnModifier.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 15;");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 6 15;");

        btnVoir.setOnAction(e -> afficherDetailsParcelle(parcelle));
        btnModifier.setOnAction(e -> ouvrirModifierParcelle(parcelle));
        btnSupprimer.setOnAction(e -> supprimerParcelle(parcelle));

        HBox buttonsBox = new HBox(10, btnModifier, btnSupprimer, btnVoir);
        buttonsBox.setStyle("-fx-padding: 10 0 0 0;");
        infoBox.getChildren().add(buttonsBox);

        card.getChildren().addAll(left, infoBox);
        return card;
    }

    private void ouvrirModifierParcelle(Parcelle parcelle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ModifierParcelle.fxml"));
            Parent root = loader.load();
            ModifierParcelleController controller = loader.getController();
            controller.setParcelle(parcelle);
            controller.setOnModificationSuccess(this::refreshCards);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Parcelle");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerParcelle(Parcelle parcelle) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment supprimer cette parcelle ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Suppression de la parcelle");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                parcelleService.supprimer(parcelle.getId());
                refreshCards();
                new Alert(Alert.AlertType.INFORMATION, "✅ Parcelle supprimée avec succès !").show();
            }
        });
    }

    private void afficherDetailsParcelle(Parcelle parcelle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/VoirParcelle.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/AjouterParcelle.fxml"));
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

    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/machine-home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
