package com.esprit.Controllers;

import com.esprit.models.Culture;
import com.esprit.services.CultureService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SuggestionCultureController {

    @FXML private TextField solField;
    @FXML private TextField saisonField;
    @FXML private VBox cardContainer;
    @FXML private ScrollPane scrollPane;

    private final CultureService cultureService = new CultureService();

    @FXML
    void suggérerCulture() {
        String sol = solField.getText().trim().toLowerCase();
        String saison = saisonField.getText().trim().toLowerCase();

        cardContainer.getChildren().clear();

        if (sol.isEmpty() || saison.isEmpty()) {
            Label erreur = new Label("❌ Veuillez renseigner le type de sol et la saison.");
            cardContainer.getChildren().add(erreur);
            return;
        }

        List<Culture> suggestions = cultureService.suggérerCulturesAméliorée(sol, saison);

        if (suggestions.isEmpty()) {
            Label vide = new Label("⚠️ Aucune culture strictement adaptée trouvée pour ce sol et cette saison.\n\n" +
                    "Essayez d'élargir vos critères ou consulter les cultures disponibles.");
            cardContainer.getChildren().add(vide);
            return;
        }

        for (Culture c : suggestions) {
            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-padding: 10; -fx-background-radius: 8;");

            Label titre = new Label("🌱 " + c.getNom() + "  (" + c.getCategorie() + ")");
            titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label saisonLbl = new Label("📅 Saison : " + c.getSaison());
            saisonLbl.setStyle("-fx-text-fill: #555;");

            Label desc = new Label("📝 " + c.getDescription());
            desc.setWrapText(true);

            ImageView image = new ImageView();
            try {
                image.setImage(new Image(c.getImage(), 200, 150, true, true));
            } catch (Exception e) {
                image.setImage(new Image(getClass().getResource("/images/default-image.jpg").toExternalForm(), 200, 150, true, true));
            }
            image.setStyle("-fx-effect: dropshadow(gaussian, gray, 4, 0.5, 1, 1); -fx-background-radius: 6;");

            // ➕ Ajouter à une parcelle
            Button ajouterBtn = new Button("➕ Ajouter à une parcelle");
            ajouterBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
            ajouterBtn.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterParcelle.fxml"));
                    Parent root = loader.load();

                    AjouterParcelleController controller = loader.getController();
                    controller.préremplirCulture(c);

                    Stage stage = new Stage();
                    stage.setTitle("Ajouter une parcelle avec culture : " + c.getNom());
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            card.getChildren().addAll(image, titre, saisonLbl, desc, ajouterBtn);
            cardContainer.getChildren().add(card);
        }
    }
}
