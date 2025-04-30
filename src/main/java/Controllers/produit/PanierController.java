package Controllers.produit;

import Entites.Commande;
import Entites.Produit;
import Services.CommandeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PanierController {

    @FXML
    private VBox panierContainer;

    private List<Produit> produitsPanier = new ArrayList<>();
    private CommandeService commandeService;
    private boolean commandePassee = false;

    // Liste des commandes affichées
    private List<Commande> commandesList = new ArrayList<>();




    public void setProduitsPanier(List<Produit> produits) {
        this.produitsPanier = produits;
        afficherPanier();
    }

    public void setCommandeService(CommandeService service) {
        this.commandeService = service;
    }

    public boolean isCommandePassee() {
        return commandePassee;
    }


    private void afficherPanier() {
        panierContainer.getChildren().clear();
        double total = 0.0;

        // Affichage des produits dans le panier
        for (Produit produit : produitsPanier) {
            VBox produitBox = new VBox();
            produitBox.setSpacing(3);

            Label nomLabel = new Label("Nom : " + produit.getNom());
            Label prixLabel = new Label("Prix : " + produit.getPrix_unitaire() + " DT");
            Button supprimerBtn = new Button("Supprimer");
            supprimerBtn.setOnAction(e -> {
                produitsPanier.remove(produit);
                afficherPanier();
            });

            produitBox.getChildren().addAll(nomLabel, prixLabel, supprimerBtn);
            produitBox.setStyle("-fx-border-color: #ccc; -fx-padding: 5; -fx-background-color: #fff;");
            panierContainer.getChildren().add(produitBox);

            total += produit.getPrix_unitaire();
        }

        // Si le panier n'est pas vide, afficher le bouton "Passer commande"
        if (!produitsPanier.isEmpty()) {
            Label totalLabel = new Label("Total : " + total + " DT");
            Button passerCommandeBtn = new Button("Passer commande");

            passerCommandeBtn.setOnAction(e -> {
                // Afficher la confirmation avant de passer commande
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation");
                confirmation.setHeaderText("Confirmer la commande");
                confirmation.setContentText("Voulez-vous vraiment passer cette commande ?");

                confirmation.showAndWait().ifPresent(response -> {
                    switch (response.getButtonData()) {
                        case OK_DONE:
                            passerCommande();  // Appel de la méthode existante
                            break;
                        case CANCEL_CLOSE:
                            afficherMessage("Commande annulée", "La commande n'a pas été envoyée.");
                            break;
                    }
                });
            });

            VBox commandeBox = new VBox(10, totalLabel, passerCommandeBtn);
            commandeBox.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-border-color: #ccc;");
            panierContainer.getChildren().add(commandeBox);
        }
    }

    @FXML
    private void passerCommande() {
        if (produitsPanier.isEmpty()) {
            afficherMessage("Panier vide", "Ajoutez des produits avant de commander.");
            return;
        }

        double total = produitsPanier.stream().mapToDouble(Produit::getPrix_unitaire).sum();
        Commande commande = new Commande("En attente", LocalDateTime.now(), total);
        commandeService.ajouterCommande(commande);
        commandesList.add(commande);  // Ajoute la commande à la liste

        afficherMessage("Commande passée", "Commande enregistrée avec succès !");
        commandePassee = true;

        // Fermer la fenêtre du panier
        Stage stage = (Stage) panierContainer.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void afficherCommandes() {
        // Vide le conteneur des anciennes commandes
        panierContainer.getChildren().clear();

        // Affichage de chaque commande avec un bouton d'action
        for (Commande commande : commandesList) {
            VBox commandeBox = new VBox();
            commandeBox.setSpacing(10);
            Label commandeLabel = new Label("Commande : " + commande.getId() + " - " + commande.getEtat());
            Label totalCommandeLabel = new Label("Total : " + commande.getTotal() + " DT");

            Button ajouterBtn = new Button("Ajouter au panier");
            ajouterBtn.setOnAction(e -> {
                // Ajouter la commande ou produits au panier (à ajuster selon logique)
                afficherMessage("Ajouté au panier", "Produit de la commande ajouté au panier.");
            });

            Button supprimerBtn = new Button("Supprimer");
            supprimerBtn.setOnAction(e -> {
                commandesList.remove(commande);
                afficherCommandes();
            });

            Button passerCommandeBtn = new Button("Passer commande");
            passerCommandeBtn.setOnAction(e -> passerCommande());

            commandeBox.getChildren().addAll(commandeLabel, totalCommandeLabel, ajouterBtn, supprimerBtn, passerCommandeBtn);
            commandeBox.setStyle("-fx-border-color: #ccc; -fx-padding: 5; -fx-background-color: #fff;");
            panierContainer.getChildren().add(commandeBox);
        }
    }

    @FXML


    private Button btnRetour;

    @FXML
    private void retourCommande(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commande.fxml")); // Remplace par le bon chemin
            Parent root = loader.load();

            // Optionnel : récupérer le contrôleur si tu veux lui passer des données
            // CommandeController controller = loader.getController();

            // Afficher la nouvelle scène
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
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