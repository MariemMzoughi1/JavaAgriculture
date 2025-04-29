package com.esprit.Controllers;

import com.esprit.models.Culture;
import com.esprit.services.CultureService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.List;

public class AfficherCultureController {

    @FXML private TextField searchCategorieField;
    @FXML private VBox conseilsBox;
    @FXML private VBox cardContainer;

    private final CultureService cultureService = new CultureService();

    public void initialize() {
        refreshCards();
    }

    @FXML
    private void refreshCards() {
        List<Culture> cultures = cultureService.afficher();
        cardContainer.getChildren().clear();

        for (Culture culture : cultures) {
            HBox card = createCultureCard(culture);
            cardContainer.getChildren().add(card);
        }

        if (cultures.isEmpty()) {
            Label videLabel = new Label("🌱 Aucune culture trouvée.");
            cardContainer.getChildren().add(videLabel);
        }
    }

    private HBox createCultureCard(Culture culture) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 5);");
        card.setPrefWidth(800);

        VBox left = new VBox();
        ImageView imageView = new ImageView();
        String imageUrl = culture.getImage();
        try {
            if (imageUrl == null || imageUrl.isBlank()) {
                throw new IllegalArgumentException("Image URL vide");
            }
            imageView.setImage(new Image(imageUrl, 200, 150, false, false));
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResource("/images/default-image.jpg").toExternalForm(), 200, 150, false, false));
        }
        imageView.setStyle("-fx-border-radius: 10; -fx-background-radius: 10;");
        left.getChildren().add(imageView);

        VBox right = new VBox(8);
        Label nom = new Label("🌿 " + culture.getNom());
        nom.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label desc = new Label(culture.getDescription());
        desc.setStyle("-fx-text-fill: #666666;");
        Label plantation = new Label("🌱 Plantation : " + culture.getDatePlantation());
        Label recolte = new Label("🌾 Récolte : " + culture.getDateRecolte());
        Label quantite = new Label("📦 Quantité : " + culture.getQuantite() + " KG");
        Label saison = new Label("🗓 Saison : " + culture.getSaison());
        Label categorie = new Label("🧪 Catégorie : " + culture.getCategorie());

        Button btnModifier = new Button("✏️ Modifier");
        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 6 14;");
        btnModifier.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCulture.fxml"));
                Parent root = loader.load();
                ModifierCultureController controller = loader.getController();
                controller.initData(culture);
                controller.setOnModificationSuccess(this::refreshCards);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Modifier Culture");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button btnSupprimer = new Button("🗑 Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 6 14;");
        btnSupprimer.setOnAction(event -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Suppression de Culture");
            confirm.setHeaderText("🗑 Voulez-vous vraiment supprimer cette culture ?");
            confirm.setContentText("Nom : " + culture.getNom() + "\nCatégorie : " + culture.getCategorie());

            DialogPane dialogPane = confirm.getDialogPane();
            dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #f8f9fa);" +
                    "-fx-border-color: #e53935;" +
                    "-fx-border-radius: 12;" +
                    "-fx-background-radius: 12;" +
                    "-fx-padding: 20;");
            Label headerLabel = (Label) dialogPane.lookup(".header-panel .label");
            if (headerLabel != null) {
                headerLabel.setStyle("-fx-text-fill: #e53935; -fx-font-size: 20px; -fx-font-weight: bold;");
            }
            Label contentLabel = (Label) dialogPane.lookup(".content.label");
            if (contentLabel != null) {
                contentLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #37474f;");
            }

            ButtonType confirmButton = new ButtonType("✅ Oui, Supprimer", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("❌ Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirm.getButtonTypes().setAll(confirmButton, cancelButton);

            confirm.showAndWait().ifPresent(response -> {
                if (response == confirmButton) {
                    boolean success = cultureService.supprimer(culture.getId());
                    if (success) {
                        refreshCards();
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Suppression réussie");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("✅ Culture supprimée avec succès !");
                        successAlert.getDialogPane().setStyle("-fx-background-color: linear-gradient(to bottom right, #e8f5e9, #ffffff);-fx-border-color: #43a047; -fx-border-radius: 12; -fx-background-radius: 12;");
                        successAlert.getDialogPane().lookup(".content.label").setStyle("-fx-font-size: 16px; -fx-text-fill: #2e7d32;");
                        successAlert.show();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Erreur");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("❌ Échec de la suppression !");
                        errorAlert.getDialogPane().setStyle("-fx-background-color: linear-gradient(to bottom right, #ffebee, #ffffff);-fx-border-color: #e53935; -fx-border-radius: 12; -fx-background-radius: 12;");
                        errorAlert.getDialogPane().lookup(".content.label").setStyle("-fx-font-size: 16px; -fx-text-fill: #c62828;");
                        errorAlert.show();
                    }
                }
            });
        });

        HBox buttons = new HBox(10, btnModifier, btnSupprimer);
        right.getChildren().addAll(nom, desc, plantation, recolte, quantite, saison, categorie, buttons);

        card.getChildren().addAll(left, right);
        return card;
    }

    public void ajouterCulture(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter_culture.fxml"));
            Parent root = loader.load();
            AjouterCultureController controller = loader.getController();
            controller.setOnAjoutSuccess(this::refreshCards);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Culture");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rechercherParCategorie() {
        String keyword = searchCategorieField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshCards();
        } else {
            List<Culture> results = cultureService.afficher().stream()
                    .filter(c -> c.getCategorie().toLowerCase().contains(keyword))
                    .toList();
            afficherCultures(results);
        }
    }

    @FXML
    private void trierParNomAsc() {
        List<Culture> sorted = cultureService.afficher().stream()
                .sorted((c1, c2) -> c1.getNom().compareToIgnoreCase(c2.getNom()))
                .toList();
        afficherCultures(sorted);
    }

    @FXML
    private void trierParNomDesc() {
        List<Culture> sorted = cultureService.afficher().stream()
                .sorted((c1, c2) -> c2.getNom().compareToIgnoreCase(c1.getNom()))
                .toList();
        afficherCultures(sorted);
    }

    @FXML
    private void trierParQuantiteAsc() {
        List<Culture> sorted = cultureService.afficher().stream()
                .sorted((c1, c2) -> Double.compare(c1.getQuantite(), c2.getQuantite()))
                .toList();
        afficherCultures(sorted);
    }

    @FXML
    private void trierParQuantiteDesc() {
        List<Culture> sorted = cultureService.afficher().stream()
                .sorted((c1, c2) -> Double.compare(c2.getQuantite(), c1.getQuantite()))
                .toList();
        afficherCultures(sorted);
    }

    private void afficherCultures(List<Culture> list) {
        cardContainer.getChildren().clear();
        if (list.isEmpty()) {
            cardContainer.getChildren().add(new Label("🌱 Aucune culture trouvée."));
        } else {
            for (Culture culture : list) {
                cardContainer.getChildren().add(createCultureCard(culture));
            }
        }
    }
}
