package controles;

import Entites.Produit;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
public class PanierController {

    @FXML private TableView<Produit> tablePanier;
    @FXML private TableColumn<Produit, String> colProduit;
    @FXML private TableColumn<Produit, Integer> colQuantite;
    @FXML private Label labelTotal;

    private ObservableList<Produit> panier;

    public PanierController() {
        panier = FXCollections.observableArrayList();  // Initialisation de la liste
    }

    // Méthode d'initialisation
    @FXML
    public void initialize() {
        // Lier les colonnes aux données
        colProduit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colQuantite.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantite_stock()).asObject());

        // Ajouter les données dans la TableView
        tablePanier.setItems(panier);

        // Calculer et afficher le total
        updateTotal();
    }

    // Méthode pour mettre à jour le panier
    public void setPanier(List<Produit> panierList) {
        // Clear l'ancienne liste et ajoute les nouveaux produits
        panier.clear();
        panier.addAll(panierList);
        tablePanier.setItems(panier);  // Mettre à jour la TableView
        updateTotal();  // Recalculer le total
    }

    // Méthode pour mettre à jour le total du panier
    private void updateTotal() {
        double total = 0;
        for (Produit produit : panier) {
            total += produit.getPrix_unitaire() * produit.getQuantite_stock();
        }
        labelTotal.setText("Total: " + total + " DT");
    }


    public void onCommander(ActionEvent actionEvent) {
        if (panier.isEmpty()) {
            afficherMessage("Panier vide", "Ajoutez des produits avant de passer une commande.");
            return;
        }

        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de commande");
        confirmation.setHeaderText("Souhaitez-vous passer la commande ?");
        confirmation.setContentText("Cliquez sur OK pour confirmer.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                double total = panier.stream().mapToDouble(p -> p.getPrix_unitaire() * p.getQuantite_stock()).sum();
                Entites.Commande commande = new Entites.Commande("En attente", java.time.LocalDateTime.now(), total);

                new Services.CommandeService().ajouterCommande(commande);

                afficherMessage("Commande réussie", "Votre commande a été enregistrée avec succès !");
                panier.clear();
                tablePanier.getItems().clear();
                updateTotal();
            }
        });
    }


    public void onViderPanier(ActionEvent actionEvent) {
        panier.clear();
        tablePanier.getItems().clear();
        updateTotal();
        afficherMessage("Panier vidé", "Tous les produits ont été retirés du panier.");
    }
    private void afficherMessage(String titre, String contenu) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    public void onRetourListeProduits(ActionEvent actionEvent) {
        try {
            // Charger la nouvelle vue (commande.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commande.fxml"));
            AnchorPane view = loader.load();

            // Créer une nouvelle scène avec la vue chargée
            Scene scene = new Scene(view);

            // Obtenir la fenêtre actuelle et changer la scène
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Commandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

