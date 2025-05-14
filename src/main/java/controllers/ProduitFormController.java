package controllers;

import entities.Produit;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ProduitService;

import java.io.IOException;
import java.time.LocalDate;

public class ProduitFormController {

    @FXML private TextField nomField, descriptionField, categorieField, prixField, stockField, imageField;

    private final ProduitService produitService = new ProduitService();
    private Produit produitExistant; // null si ajout

    public void initData(Produit produit) {
        this.produitExistant = produit;
        if (produit != null) {
            nomField.setText(produit.getNom());
            descriptionField.setText(produit.getDescription());
            categorieField.setText(produit.getCategorie());
            prixField.setText(String.valueOf(produit.getPrix_unitaire()));
            stockField.setText(String.valueOf(produit.getQuantite_stock()));
            imageField.setText(produit.getImage());
        }
    }

    @FXML
    private void handleSave() {
        String nom = nomField.getText();
        String desc = descriptionField.getText();
        String cat = categorieField.getText();
        int prix = Integer.parseInt(prixField.getText());
        int stock = Integer.parseInt(stockField.getText());
        String image = imageField.getText();

        if (produitExistant == null) {
            Produit nouveau = new Produit(nom, desc, image, cat, prix, stock, 1, LocalDate.now()); // agriculteur_id fictif
            produitService.add(nouveau);
        } else {
            produitExistant.setNom(nom);
            produitExistant.setDescription(desc);
            produitExistant.setCategorie(cat);
            produitExistant.setPrix_unitaire(prix);
            produitExistant.setQuantite_stock(stock);
            produitExistant.setImage(image);
            produitService.update(produitExistant);
        }

        retourner();
    }

    @FXML
    private void handleRetour() {
        retourner();
    }

    private void retourner() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/produit-admin-view.fxml"));
            VBox root = loader.load();
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
