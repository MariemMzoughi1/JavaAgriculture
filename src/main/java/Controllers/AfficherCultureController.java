package Controllers;

import Entites.Culture;
import Services.CultureService;
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

import java.io.IOException;
import java.util.List;

public class AfficherCultureController {
    @FXML private TextField searchCategorieField;

    @FXML
    private VBox cardContainer;

    private final CultureService cultureService = new CultureService();

    public void initialize() {
        cardContainer.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
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

        // ✅ ImageView avec vérification URL
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

        // 📋 Détails de la culture
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

        // 🖊 Modifier
        Button btnModifier = new Button("✏️ Modifier");
        btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
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

        // 🗑 Supprimer
        Button btnSupprimer = new Button("🗑 Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnSupprimer.setOnAction(event -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Voulez-vous vraiment supprimer cette culture ?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmation");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    boolean success = cultureService.supprimer(culture.getId());
                    if (success) {
                        refreshCards();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Culture supprimée !");
                        alert.show();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "❌ Échec de la suppression.");
                        alert.show();
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

    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/machine-home.fxml")); // remplace le chemin
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
