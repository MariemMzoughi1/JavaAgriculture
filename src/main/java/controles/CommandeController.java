package controles;

import Entites.Commande;
import Entites.Produit;
import Services.CommandeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandeController {

    @FXML
    private FlowPane produitsContainer;

    @FXML
    private Label panierMessage;

    @FXML
    private Button btnPanier;

    @FXML
    private ComboBox<String> categorieFilter;

    @FXML
    private ComboBox<String> prixSort;

    private final CommandeService commandeService = new CommandeService();
    private final List<Produit> produitsDisponibles = new ArrayList<>();
    private final List<Produit> panier = new ArrayList<>();

    @FXML
    public void initialize() {
        produitsDisponibles.addAll(commandeService.getProduits());
        chargerCategories();
        afficherProduits(produitsDisponibles);
        mettreAJourPanierMessage();
    }

    private void chargerCategories() {
        List<String> categories = produitsDisponibles.stream()
                .map(Produit::getCategorie)
                .distinct()
                .toList();
        categorieFilter.getItems().addAll(categories);
    }

    private void afficherProduits(List<Produit> produits) {
        produitsContainer.getChildren().clear();
        for (Produit produit : produits) {
            VBox card = creerCarteProduit(produit);
            produitsContainer.getChildren().add(card);
        }
    }

    private void afficherProduits() {
        afficherProduits(produitsDisponibles);
    }

    private VBox creerCarteProduit(Produit produit) {
        VBox card = new VBox(10);
        card.setPrefWidth(220);
        card.setAlignment(Pos.CENTER);
        card.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 10;
            -fx-padding: 15;
            -fx-border-color: #dddddd;
            -fx-border-radius: 10;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);
        """);

        ImageView imageView = new ImageView();
        try {
            Image image = new Image("file:" + produit.getImage(), 200, 150, true, true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Erreur chargement image : " + produit.getImage());
        }
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Label nomLabel = new Label(produit.getNom());
        nomLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label prixLabel = new Label(produit.getPrix_unitaire() + " DT");
        prixLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #5b5b5b;");

        Button btnAjouter = new Button("Ajouter au panier");
        btnAjouter.setStyle("""
            -fx-background-color: #4CAF50;
            -fx-text-fill: white;
            -fx-padding: 10px 20px;
            -fx-border-radius: 8px;
            -fx-font-size: 13;
            -fx-cursor: hand;
        """);
        btnAjouter.setOnAction(e -> ajouterAuPanier(produit));

        card.getChildren().addAll(imageView, nomLabel, prixLabel, btnAjouter);
        return card;
    }

    private void ajouterAuPanier(Produit produit) {
        panier.add(produit);
        afficherMessage("Succès", "Produit ajouté au panier !");
        mettreAJourPanierMessage();
    }

    private void mettreAJourPanierMessage() {
        if (panier.isEmpty()) {
            panierMessage.setText("Panier vide !");
        } else {
            panierMessage.setText("Vous avez " + panier.size() + " produit(s) dans votre panier.");
        }
        panierMessage.setVisible(true);
    }

    @FXML
    private void appliquerFiltres(ActionEvent event) {
        String selectedCategorie = categorieFilter.getValue();
        String selectedTri = prixSort.getValue();

        List<Produit> filtres = new ArrayList<>(produitsDisponibles);

        if (selectedCategorie != null && !selectedCategorie.isEmpty()) {
            filtres.removeIf(p -> !p.getCategorie().equalsIgnoreCase(selectedCategorie));
        }

        if ("Prix croissant".equals(selectedTri)) {
            filtres.sort((p1, p2) -> Double.compare(p1.getPrix_unitaire(), p2.getPrix_unitaire()));
        } else if ("Prix décroissant".equals(selectedTri)) {
            filtres.sort((p1, p2) -> Double.compare(p2.getPrix_unitaire(), p1.getPrix_unitaire()));
        }

        afficherProduits(filtres);
    }

    @FXML
    private void passerCommandeDepuisPagePrincipale(ActionEvent event) {
        if (panier.isEmpty()) {
            afficherMessage("Panier vide", "Ajoutez des produits avant de passer une commande.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de commande");
        confirmation.setHeaderText("Souhaitez-vous passer la commande ?");
        confirmation.setContentText("Cliquez sur OUI pour confirmer.");

        confirmation.showAndWait().ifPresent(response -> {
            switch (response.getButtonData()) {
                case OK_DONE -> {
                    double total = panier.stream().mapToDouble(Produit::getPrix_unitaire).sum();
                    Commande commande = new Commande("Validé", java.time.LocalDateTime.now(), total);
                    commandeService.ajouterCommande(commande);
                    afficherMessage("Commande enregistrée", "Votre commande a été passée avec succès !");
                    panier.clear();
                    mettreAJourPanierMessage();
                }
                case CANCEL_CLOSE -> afficherMessage("Commande annulée", "La commande n’a pas été envoyée.");
            }
        });
    }

    @FXML
    private void ouvrirPanier(ActionEvent event) {
        try {
            if (panier.isEmpty()) {
                afficherMessage("Panier vide", "Votre panier est actuellement vide.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/panier.fxml"));
            AnchorPane panierView = loader.load();

            PanierController controller = loader.getController();
            controller.setPanier(panier);

            Scene panierScene = new Scene(panierView);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(panierScene);
            stage.setTitle("Panier");
            stage.show();
        } catch (IOException e) {
            afficherMessage("Erreur", "Impossible de charger la vue du panier.");
        }
    }

    @FXML
    private void retourAfficherProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherproduit.fxml"));
            AnchorPane view = loader.load();

            Scene scene = new Scene(view);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Produits");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void afficherMessage(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
