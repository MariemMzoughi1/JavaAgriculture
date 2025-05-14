package controllers;

import entities.Produit;
import services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class AfficherProduit {

    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, LocalDate> colDate;
    @FXML private Button btnAjouter;

    private final ProduitService produitService = new ProduitService();

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

    private void chargerProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.find());
        tableProduits.setItems(produits);
    }

    private void onInfoClicked(Produit produit) {
        if (produit != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/AfficherInfoProduit.fxml"));
                Parent root = loader.load();

                AfficherInfoProduit controller = loader.getController();
                controller.initialize(produit);
                controller.setProduitUpdateListener(this::chargerProduits);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Informations du produit");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onDeleteClicked(Produit produit) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                produitService.delete(produit);
                chargerProduits();
                new Alert(Alert.AlertType.INFORMATION, "Produit supprimé avec succès !").show();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Suppression annulée.").show();
            }
        });
    }

    @FXML
    private void ajouterProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/AjouterProduit.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirCommandes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/commande.fxml"));
            AnchorPane commandeView = loader.load();

            Scene scene = new Scene(commandeView);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Commandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ouvrirStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/statistique.fxml"));
            Parent content = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Statistiques");
            stage.setScene(new Scene(content));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
