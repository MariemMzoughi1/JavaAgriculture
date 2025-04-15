package controles;

import Entites.Produit;
import Services.ProduitService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

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
    void ajouterProduit(ActionEvent event) {
        try {
            String nom = nomTextField.getText();
            String description = descriptionTextField.getText();
            String categorie = categorieTextField.getText();
            int prixUnitaire = Integer.parseInt(prixUnitaireTextField.getText());
            int quantiteStock = Integer.parseInt(quantiteStockTextField.getText());
            String image = imageTextField.getText();

            Produit produit = new Produit();
            produit.setNom(nom);
            produit.setDescription(description);
            produit.setCategorie(categorie);
            produit.setPrix_unitaire(prixUnitaire);
            produit.setQuantite_stock(quantiteStock);
            produit.setImage(image);

            produit.setAgriculteur_id(1); // Id par défaut ou récupéré depuis le user connecté
            produit.setDate_ajout(LocalDate.now());

            ProduitService produitService = new ProduitService();
            produitService.add(produit);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Produit Ajouté avec succés");
            alert.show();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();        }
    }


    }


