package controles;

import Entites.Produit;
import Services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;

public class AfficherProduit {

    @FXML
    private TableView<Produit> tableProduits;

    @FXML
    private TableColumn<Produit, String> colNom;

    @FXML
    private Button btnAjouter;

    @FXML
    private TableColumn<Produit, LocalDate> colDate;

    ProduitService produitService = new ProduitService();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date_ajout"));

        TableColumn<Produit, Void> colActions = new TableColumn<>("Actions");

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnInfo = new Button("Information");
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDelete = new Button("Supprimer");

            {
                btnInfo.setStyle("-fx-background-color: #288edf; -fx-text-fill: white;");
                btnEdit.setStyle("-fx-background-color: #41e17a; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #f31f10; -fx-text-fill: white;");

                btnInfo.setOnAction(event -> onInfoClicked(getTableRow().getItem()));
                btnEdit.setOnAction(event -> onEditClicked(getTableRow().getItem()));
                btnDelete.setOnAction(event -> onDeleteClicked(getTableRow().getItem()));
            }
            private final HBox pane = new HBox(10, btnInfo, btnEdit, btnDelete);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableProduits.getColumns().add(colActions);

        ObservableList<Produit> produits = FXCollections.observableArrayList(produitService.find());
        tableProduits.setItems(produits);
    }


    private void onInfoClicked(Produit produit) {
        if (produit != null) {
            try {
                // Charger le fichier FXML pour la fenêtre des informations du produit
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherInfoProduit.fxml"));
                Parent root = loader.load();

                // Initialiser le contrôleur de la fenêtre des informations du produit
                AfficherInfoProduit infoController = loader.getController();
                infoController.initialize(produit);  // Passer le produit aux informations

                // Ouvrir une nouvelle fenêtre pour afficher les informations du produit
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Informations du produit");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void onEditClicked(Produit produit) {
        if (produit != null) {
            System.out.println("Modifier : " + produit.getNom());
        }
    }

    private void onDeleteClicked(Produit produit) {
        // Demande de confirmation avant suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                produitService.delete(produit);
                tableProduits.getItems().remove(produit);
                System.out.println("Produit supprimé : " + produit.getNom());

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setContentText("Produit supprimé avec succès !");
                successAlert.show();
            } else {
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setContentText("Suppression Annulée !");
                successAlert.show();            }
        });
    }


    @FXML
    private void ajouterProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduit.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnAjouter.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un produit");



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

