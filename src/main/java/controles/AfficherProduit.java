package controles;

import Entites.Produit;
import Services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class AfficherProduit {

    @FXML
    private TableView<Produit> tableProduits;

    @FXML
    private TableColumn<Produit, String> colNom;

    @FXML
    private TableColumn<Produit, LocalDate> colDate;

    @FXML
    private Button btnAjouter;

    ProduitService produitService = new ProduitService();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date_ajout"));

        TableColumn<Produit, Void> colActions = new TableColumn<>("Actions");

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnInfo = new Button("Information");
            private final Button btnDelete = new Button("Supprimer");

            {
                btnInfo.setStyle("-fx-background-color: #288edf; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #f31f10; -fx-text-fill: white;");

                btnInfo.setOnAction(event -> onInfoClicked(getTableRow().getItem()));
                btnDelete.setOnAction(event -> onDeleteClicked(getTableRow().getItem()));
            }

            private final HBox pane = new HBox(10, btnInfo, btnDelete);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableProduits.getColumns().add(colActions);

        chargerProduits();
    }

    @FXML
    private void ouvrirStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/statistique.fxml"));
            Node content = loader.load();
            // Remplace mainContent par le bon conteneur où tu veux afficher la vue, par exemple :
            // mainContent.getChildren().setAll(content);
            // ou bien ouvre une nouvelle fenêtre :
            Stage stage = new Stage();
            stage.setTitle("Statistiques");
            stage.setScene(new Scene((Parent) content));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ✅ Méthode pour recharger les données de la table
    private void chargerProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.find());
        tableProduits.setItems(produits);
    }

    // ✅ Méthode affichant les informations du produit
    private void onInfoClicked(Produit produit) {
        if (produit != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherInfoProduit.fxml"));
                Parent root = loader.load();

                AfficherInfoProduit controller = loader.getController();
                controller.initialize(produit);

                // Rafraîchir la table si modification depuis la fenêtre info
                controller.setProduitUpdateListener(() -> chargerProduits());

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Informations du produit");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ✅ Méthode de suppression
    private void onDeleteClicked(Produit produit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                produitService.delete(produit);
                chargerProduits(); // Rafraîchir la table
                System.out.println("Produit supprimé : " + produit.getNom());

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setContentText("Produit supprimé avec succès !");
                successAlert.show();
            } else {
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setContentText("Suppression annulée.");
                infoAlert.show();
            }
        });
    }

    // ✅ Méthode pour ajouter un nouveau produit
    @FXML
    private void ajouterProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
