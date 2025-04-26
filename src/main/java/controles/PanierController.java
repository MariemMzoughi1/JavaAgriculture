package controles;

import Entites.Produit;
import Services.CommandeService;
import Entites.Commande;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class PanierController {

    @FXML private TableView<PanierItem> tablePanier;
    @FXML private TableColumn<PanierItem, String> colProduit;
    @FXML private TableColumn<PanierItem, Integer> colQuantite;
    @FXML private TableColumn<PanierItem, Void> colActions;
    @FXML private Label labelTotal;
    @FXML private Button btnVider, btnCommander, btnRetour;
    @FXML
    private VBox formulaireVBox;
    private ObservableList<PanierItem> panier;

    public PanierController() {
        panier = FXCollections.observableArrayList(); // Initialisation
    }

    @FXML
    public void initialize() {
        // Lier les colonnes aux propriétés de PanierItem
        colProduit.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
        colQuantite.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());

        tablePanier.setItems(panier);
        ajouterBoutonsActions();
        updateTotal();
    }

    public void setPanier(List<Produit> panierList) {
        panier.clear();
        for (Produit produit : panierList) {
            panier.add(new PanierItem(produit));
        }
        tablePanier.setItems(panier);
        updateTotal();
    }

    private void updateTotal() {
        double total = 0;
        for (PanierItem item : panier) {
            total += item.getTotalPrix();
        }
        labelTotal.setText("Total: " + total + " DT");
    }

    private void ajouterBoutonsActions() {
        colActions.setCellFactory(param -> new TableCell<PanierItem, Void>() {
            private final Button btnPlus = new Button("+");
            private final Button btnMoins = new Button("-");

            {
                btnPlus.setOnAction(event -> {
                    PanierItem item = getTableView().getItems().get(getIndex());
                    item.setQuantite(item.getQuantite() + 1);
                    tablePanier.refresh();
                    updateTotal();
                });

                btnMoins.setOnAction(event -> {
                    PanierItem item = getTableView().getItems().get(getIndex());
                    if (item.getQuantite() > 1) {
                        item.setQuantite(item.getQuantite() - 1);
                        tablePanier.refresh();
                        updateTotal();
                    }
                });
            }

            private final HBox pane = new HBox(10, btnPlus, btnMoins);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    @FXML
    public void onCommander(ActionEvent actionEvent) {
        if (panier.isEmpty()) {
            afficherMessage("Panier vide", "Ajoutez des produits avant de passer une commande.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FormulaireCommande.fxml"));
            AnchorPane formulaireView = loader.load();
            // Passer le total au formulaire
            FormulaireCommandeController controller = loader.getController();
            double total = panier.stream().mapToDouble(PanierItem::getTotalPrix).sum();
            controller.setTotalCommande(total);

            // Ouvrir une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Formulaire de Commande");
            stage.setScene(new Scene(formulaireView));
            stage.show();

        }  catch (IOException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de Chargement");
            alert.setContentText("Erreur : " + e.getMessage());
        alert.showAndWait();
    }

}


    @FXML
    public void onViderPanier(ActionEvent actionEvent) {
        panier.clear();
        tablePanier.getItems().clear();
        updateTotal();
        afficherMessage("Panier vidé", "Tous les produits ont été retirés du panier.");
    }

    private void afficherMessage(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    @FXML
    public void onRetourListeProduits(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commande.fxml"));
            AnchorPane view = loader.load();
            Scene scene = new Scene(view);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Commandes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
