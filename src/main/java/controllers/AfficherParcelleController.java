package controllers;

import entities.Parcelle;
import javafx.scene.Node;
import services.ParcelleService;
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
import java.util.List;

public class AfficherParcelleController {
    @FXML private TextField searchField;
    @FXML
    private VBox cardContainer;

    private final ParcelleService parcelleService = new ParcelleService();

    public void initialize() {
        refreshCards();
    }

    @FXML
    private void refreshCards() {
        afficherParcelles(parcelleService.afficher());
    }
    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/machine-home.fxml")); // remplace le chemin
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private HBox createParcelleCard(Parcelle parcelle) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 5);");
        card.setPrefWidth(800);

        // Image à gauche
        VBox left = new VBox();
        ImageView imageView = new ImageView();
        try {
            String imageUrl = parcelle.getImage();
            if (imageUrl == null || imageUrl.isBlank()) {
                throw new IllegalArgumentException("Image vide");
            }
            imageView.setImage(new Image(imageUrl, 200, 150, false, false));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResource("/images/default-image.jpg").toExternalForm(), 200, 150, false, false));
        }
        imageView.setStyle("-fx-border-radius: 10; -fx-background-radius: 10;");
        left.getChildren().add(imageView);

        // Détails à droite
        VBox infoBox = new VBox(8);
        Label titre = new Label("🧺 Parcelle - Zone : " + parcelle.getZone());
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        infoBox.getChildren().addAll(
                titre,
                new Label("Culture ID : " + parcelle.getCultureActuelleId()),
                new Label("📝 Description : " + parcelle.getDescription()),
                new Label("📐 Superficie : " + parcelle.getSuperficie() + " m²"),
                new Label("💰 Prix : " + parcelle.getPrixDeLocation() + " DT"),
                new Label("📅 Début : " + parcelle.getDateDeLocation()),
                new Label("📅 Fin : " + parcelle.getDateDeFinLocation()),
                new Label("⚠ État : " + parcelle.getEtat()),
                new Label("🌍 Sol : " + parcelle.getTypeSol())
        );

        // Boutons
        Button btnModifier = new Button("✏️ Modifier");
        Button btnSupprimer = new Button("🗑 Supprimer");
        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        btnModifier.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ModifierParcelle.fxml"));
                Parent root = loader.load();
                ModifierParcelleController controller = loader.getController();
                controller.setParcelle(parcelle);

// ✅ Rafraîchir après modification
                controller.setOnModificationSuccess(this::refreshCards);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier Parcelle");
                stage.showAndWait();  // attend la fermeture
                refreshCards(); // ✅ optionnel si déjà appelé dans callback

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnSupprimer.setOnAction(event -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment supprimer cette parcelle ?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    parcelleService.supprimer(parcelle.getId());
                    refreshCards();
                    new Alert(Alert.AlertType.INFORMATION, "✅ Parcelle supprimée !").show();
                }
            });
        });

        HBox buttons = new HBox(10, btnModifier, btnSupprimer);
        infoBox.getChildren().add(buttons);

        card.getChildren().addAll(left, infoBox);
        return card;
    }

    public void ajouterParcelle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/AjouterParcelle.fxml"));
            Parent root = loader.load();
            AjouterParcelleController controller = loader.getController();

            // 🔁 Définir la fonction de rappel pour rafraîchir après ajout
            controller.setOnAjoutSuccess(this::refreshCards);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Parcelle");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void afficherParcelles(List<Parcelle> parcelles) {
        cardContainer.getChildren().clear();
        if (parcelles.isEmpty()) {
            Label emptyLabel = new Label("🌾 Aucune parcelle trouvée.");
            cardContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Parcelle parcelle : parcelles) {
            HBox card = createParcelleCard(parcelle);
            cardContainer.getChildren().add(card);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.show();
    }
    @FXML
    private void refreshCards(ActionEvent event) {
        afficherParcelles(parcelleService.afficher());
    }


    public void rechercherParEtat(javafx.scene.input.KeyEvent keyEvent) {
        String etatRecherche = searchField.getText().trim().toLowerCase();

        if (etatRecherche.isEmpty()) {
            afficherParcelles(parcelleService.afficher());
            return;
        }

        List<Parcelle> resultatFiltre = parcelleService.afficher()
                .stream()
                .filter(p -> p.getEtat().toLowerCase().contains(etatRecherche))
                .toList();

        afficherParcelles(resultatFiltre);
    }
}
