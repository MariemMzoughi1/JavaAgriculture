package controles;

import Entites.Produit;
import Services.ProduitService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterProduit {

    @FXML
    private TextField nomTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField categorieTextField;
    @FXML
    private TextField prixUnitaireTextField;
    @FXML
    private TextField quantiteStockTextField;
    @FXML
    private TextField imageTextField;

    @FXML
    private ImageView imagePreview;

    private File selectedImageFile;

    // Bouton pour choisir une image
    @FXML
    void choisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        // Récupération de la fenêtre active
        Stage stage = (Stage) nomTextField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            selectedImageFile = selectedFile;
            imageTextField.setText(selectedFile.getAbsolutePath());
            imagePreview.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    // Bouton pour ajouter un produit
    @FXML
    void ajouterProduit(ActionEvent event) {
        String nom = nomTextField.getText().trim();
        String description = descriptionTextField.getText().trim();
        String categorie = categorieTextField.getText().trim();
        String prixStr = prixUnitaireTextField.getText().trim();
        String quantiteStr = quantiteStockTextField.getText().trim();

        // Vérifier que tous les champs sont remplis
        if (nom.isEmpty() || description.isEmpty() || categorie.isEmpty()
                || prixStr.isEmpty() || quantiteStr.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.", Alert.AlertType.WARNING);
            return;
        }

        // Vérifier que le nom ne contient pas de chiffres
        if (!nom.matches("[a-zA-Z\\s]+")) {
            showAlert("Le nom du produit ne doit contenir que des lettres.", Alert.AlertType.WARNING);
            return;
        }

        // Vérifier que le prix et la quantité sont des entiers positifs
        int prixUnitaire;
        int quantiteStock;

        try {
            prixUnitaire = Integer.parseInt(prixStr);
            quantiteStock = Integer.parseInt(quantiteStr);

            if (prixUnitaire <= 0 || quantiteStock <= 0) {
                showAlert("Le prix et la quantité doivent être supérieurs à 0.", Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Prix ou quantité invalide. Veuillez entrer des valeurs numériques valides.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification image
        if (selectedImageFile == null) {
            showAlert("Veuillez sélectionner une image pour le produit.", Alert.AlertType.WARNING);
            return;
        }

        // Création de l'objet Produit
        Produit produit = new Produit();
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setCategorie(categorie);
        produit.setPrix_unitaire(prixUnitaire);
        produit.setQuantite_stock(quantiteStock);
        produit.setImage(selectedImageFile.getAbsolutePath());
        produit.setAgriculteur_id(1); // à remplacer selon l'utilisateur connecté
        produit.setDate_ajout(LocalDate.now());

        new ProduitService().add(produit);

        // Afficher un message de succès
        showAlert("Produit ajouté avec succès !", Alert.AlertType.INFORMATION);

        // Changer de vue pour la liste des produits
        allerVersListeProduits();

    }

    // Méthode pour changer la scène et afficher la liste des produits
    private void allerVersListeProduits() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduit.fxml"));
            Parent root = loader.load();

            // Récupérer la fenêtre active et changer son contenu
            Stage stage = (Stage) nomTextField.getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de l'affichage de la liste des produits.", Alert.AlertType.ERROR);
        }
    }

    // Alerte réutilisable
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}