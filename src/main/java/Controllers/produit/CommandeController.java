package Controllers.produit;

import Entites.Commande;
import Entites.Produit;
import Services.CommandeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    private final CommandeService commandeService = new CommandeService();
    private final List<Produit> produitsDisponibles = new ArrayList<>();
    private final List<Produit> panier = new ArrayList<>();

    @FXML
    private Button btnPanier; // Ajout du bouton Panier

    @FXML
    public void initialize() {
        produitsDisponibles.addAll(commandeService.getProduits());
        afficherProduits();
        mettreAJourPanierMessage(); // Met à jour l'état initial du panier
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

    private void afficherProduits() {
        produitsContainer.getChildren().clear();

        for (Produit produit : produitsDisponibles) {
            VBox card = creerCarteProduit(produit);
            produitsContainer.getChildren().add(card);
        }
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

        // Image du produit
        ImageView imageView = new ImageView();
        try {
            Image image = new Image("file:" + produit.getImage(), 200, 150, true, true);
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Erreur de chargement d'image : " + produit.getImage());
        }
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Nom du produit
        Label nomLabel = new Label(produit.getNom());
        nomLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Prix
        Label prixLabel = new Label(produit.getPrix_unitaire() + " DT");
        prixLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #5b5b5b;");

        // Bouton "Ajouter au panier"
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
    private void passerCommandeDepuisPagePrincipale(ActionEvent event) {
        if (panier.isEmpty()) {
            afficherMessage("Panier vide", "Ajoutez des produits avant de passer une commande.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de commande");
        confirmation.setHeaderText("Souhaitez-vous passer la commande ?");
        confirmation.setContentText("Cliquez sur OUI pour confirmer.");

        // Attente de la réponse
        confirmation.showAndWait().ifPresent(response -> {
            switch (response.getButtonData()) {
                case OK_DONE -> {
                    double total = panier.stream().mapToDouble(Produit::getPrix_unitaire).sum();
                    Commande commande = new Commande("En attente", java.time.LocalDateTime.now(), total);
                    commandeService.ajouterCommande(commande);
                    afficherMessage("Commande passée", "Votre commande a été enregistrée avec succès !");
                    panier.clear(); // vider le panier
                    mettreAJourPanierMessage();
                }
                case CANCEL_CLOSE -> afficherMessage("Commande annulée", "La commande n'a pas été envoyée.");
            }
        });
    }


    // Méthode pour afficher un message d'alerte
    private void afficherMessage(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    // Action du bouton Panier
    @FXML
    public void ouvrirPanier(ActionEvent event) {
        try {
            // Charger le fichier FXML du panier
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/panier.fxml"));
            AnchorPane panierView = loader.load();

            // Créer une nouvelle scène à partir du panier FXML
            Scene panierScene = new Scene(panierView);

            // Récupérer la fenêtre actuelle (stage) et définir la nouvelle scène
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(panierScene);
            stage.show();
        } catch (IOException e) {
            afficherMessage("Erreur de chargement", "Impossible de charger la vue du panier.");
        }
    }
}
