package controllers;

import entities.Produit;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ProduitService;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProduitAdminController {

    @FXML private TableView<Produit> tableProduits;
    @FXML private TableColumn<Produit, Integer> colId;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, String> colCategorie;
    @FXML private TableColumn<Produit, Integer> colPrix;
    @FXML private TableColumn<Produit, Integer> colQuantite;
    @FXML private TableColumn<Produit, String> colDate;
    @FXML private TableColumn<Produit, ImageView> colImage;
    @FXML private TableColumn<Produit, Void> colActions;

    private final ProduitService produitService = new ProduitService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getId()).asObject());
        colNom.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNom()));
        colCategorie.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCategorie()));
        colPrix.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getPrix_unitaire()).asObject());
        colQuantite.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getQuantite_stock()).asObject());
        colDate.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDate_ajout().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        colImage.setCellValueFactory(cd -> {
            String imgPath = cd.getValue().getImage();
            ImageView imageView;
            try {
                File file = new File(imgPath);
                Image img = new Image(file.toURI().toString(), 80, 60, true, true);
                imageView = new ImageView(img);
            } catch (Exception e) {
                imageView = new ImageView(new Image("/default.png", 80, 60, true, true)); // image par défaut
            }
            return new javafx.beans.property.SimpleObjectProperty<>(imageView);
        });

        addActionButtons();
        refreshTable();
    }

    private void refreshTable() {
        ObservableList<Produit> list = FXCollections.observableArrayList(produitService.find());
        tableProduits.setItems(list);
    }

    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑️");
            private final Button btnEdit = new Button("✏️");

            {
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");

                btnDelete.setOnAction(e -> {
                    Produit produit = getTableView().getItems().get(getIndex());
                    produitService.delete(produit);
                    refreshTable();
                });

                btnEdit.setOnAction(e -> {
                    try {
                        Produit produit = getTableView().getItems().get(getIndex());
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/produit-form-view.fxml"));
                        VBox formView = loader.load();

                        ProduitFormController controller = loader.getController();
                        controller.initData(produit); // transmettre le produit

                        Stage stage = (Stage) tableProduits.getScene().getWindow();
                        stage.setScene(new Scene(formView));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(btnEdit, btnDelete);
                    box.setSpacing(10);
                    setGraphic(box);
                }
            }
        });
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/admin-dashboard-view.fxml"));
            VBox root = loader.load();
            Stage stage = (Stage) tableProduits.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/produit-form-view.fxml"));
            VBox formView = loader.load();
            Stage stage = (Stage) tableProduits.getScene().getWindow();
            stage.setScene(new Scene(formView));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
