package Controllers;

import Entites.Produit;
import Services.ProduitService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;

public class AfficherInfoProduit {

    @FXML private Label labelId, labelNom, labelDate, labelDescription, labelCategorie;
    @FXML private Label labelPrix, labelQuantiteStock, labelAgriculteurId;
    @FXML
    private ImageView imageViewProduit;

    @FXML private Button btnSave, btnCancel, btnEdit;
    @FXML
    private TextField imageField;

    @FXML
    private ImageView imagePreview;

    private File selectedImageFile;


    private TextField textFieldNom, textFieldDescription, textFieldPrix, textFieldQuantiteStock;

    private Produit produit;

    private ProduitUpdateListener updateListener;

    // Pour recevoir la référence du listener
    public void setProduitUpdateListener(ProduitUpdateListener listener) {
        this.updateListener = listener;
    }

    // Initialise les données du produit
    public void initialize(Produit produit) {
        this.produit = produit;
        if (produit != null) {
            labelId.setText(String.valueOf(produit.getId()));
            labelNom.setText(produit.getNom());
            labelDate.setText(produit.getDate_ajout().toString());
            labelDescription.setText(produit.getDescription());
            labelCategorie.setText(produit.getCategorie());
            labelPrix.setText(produit.getPrix_unitaire() + " DT");
            labelQuantiteStock.setText(String.valueOf(produit.getQuantite_stock()));
            labelAgriculteurId.setText(String.valueOf(produit.getAgriculteur_id()));

            // Chargement de l'image
            try {
                String imagePath = "file:" + produit.getImage();
                System.out.println("Chemin image : " + imagePath); // Debug
                Image image = new Image(imagePath);
                imageViewProduit.setImage(image); // Affichage correct
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }}



    // Fermer la fenêtre
    @FXML
    private void onClose() {
        ((Stage) labelNom.getScene().getWindow()).close();
    }

    // Activer le mode édition
    @FXML
    private void onEditClicked() {
        textFieldNom = new TextField(labelNom.getText());
        textFieldDescription = new TextField(labelDescription.getText());
        textFieldPrix = new TextField(labelPrix.getText());
        textFieldQuantiteStock = new TextField(labelQuantiteStock.getText());

        labelNom.setGraphic(textFieldNom);
        labelDescription.setGraphic(textFieldDescription);
        labelPrix.setGraphic(textFieldPrix);
        labelQuantiteStock.setGraphic(textFieldQuantiteStock);

        btnSave.setVisible(true);
        btnCancel.setVisible(true);
        btnEdit.setVisible(false);
    }

    // Enregistrer les modifications
    @FXML
    private void saveChanges() {
        String newNom = textFieldNom.getText().trim();
        String newDescription = textFieldDescription.getText().trim();
        String prixStr = textFieldPrix.getText().trim();
        String quantiteStr = textFieldQuantiteStock.getText().trim();

        if (newNom.isEmpty() || newDescription.isEmpty() || prixStr.isEmpty() || quantiteStr.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.", Alert.AlertType.WARNING);
            return;
        }

        try {
            double prix = Double.parseDouble(prixStr);
            int quantite = Integer.parseInt(quantiteStr);

            if (prix < 0 || quantite < 0) {
                showAlert("Le prix et la quantité doivent être positifs.", Alert.AlertType.WARNING);
                return;
            }

            // Mise à jour de l'objet produit
            produit.setNom(newNom);
            produit.setDescription(newDescription);
            produit.setPrix_unitaire((int) prix);
            produit.setQuantite_stock(quantite);

            // Mise à jour dans la base de données
            new ProduitService().update(produit);

            // Mise à jour des labels
            labelNom.setText(newNom);
            labelDescription.setText(newDescription);
            labelPrix.setText(String.valueOf(prix));
            labelQuantiteStock.setText(String.valueOf(quantite));

            // Nettoyage des TextFields
            labelNom.setGraphic(null);
            labelDescription.setGraphic(null);
            labelPrix.setGraphic(null);
            labelQuantiteStock.setGraphic(null);

            btnSave.setVisible(false);
            btnCancel.setVisible(false);
            btnEdit.setVisible(true);

            // Appeler le listener pour rafraîchir la liste principale
            if (updateListener != null) {
                updateListener.onProduitUpdated();
            }

            showAlert("Modification enregistrée avec succès !", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            showAlert("Le prix doit être un nombre décimal et la quantité un entier.", Alert.AlertType.ERROR);
        }
    }

    // Annuler les modifications
    @FXML
    private void cancelChanges() {
        labelNom.setGraphic(null);
        labelDescription.setGraphic(null);
        labelPrix.setGraphic(null);
        labelQuantiteStock.setGraphic(null);

        btnSave.setVisible(false);
        btnCancel.setVisible(false);
        btnEdit.setVisible(true);
    }

    // Méthode utilitaire pour afficher une alerte
    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Interface pour le callback
    public interface ProduitUpdateListener {
        void onProduitUpdated();
    }
}
