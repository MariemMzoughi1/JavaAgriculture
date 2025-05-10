package com.esprit.Controllers;

import com.esprit.models.Parcelle;
import com.esprit.services.ParcelleService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AfficherParcelleBackController {

    @FXML private TableView<Parcelle> parcelleTable;
    @FXML private TableColumn<Parcelle, Integer> idCol;
    @FXML private TableColumn<Parcelle, String> descriptionCol;
    @FXML private TableColumn<Parcelle, String> zoneCol;
    @FXML private TableColumn<Parcelle, Double> superficieCol;
    @FXML private TableColumn<Parcelle, String> etatCol;
    @FXML private TableColumn<Parcelle, String> typeSolCol;
    @FXML private TableColumn<Parcelle, Void> actionCol;
    @FXML private TableColumn<Parcelle, Void> imageCol;

    private final ParcelleService parcelleService = new ParcelleService();
    private final ObservableList<Parcelle> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        zoneCol.setCellValueFactory(new PropertyValueFactory<>("zone"));
        superficieCol.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        etatCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
        typeSolCol.setCellValueFactory(new PropertyValueFactory<>("typeSol"));

        loadData();
        ajouterColonneImage();
        ajouterColonneActions();
    }

    private void loadData() {
        data.clear();
        data.addAll(parcelleService.afficher());
        parcelleTable.setItems(data);
    }

    private void ajouterColonneActions() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("✏️ Modifier");
            private final Button btnSupprimer = new Button("🗑 Supprimer");

            {
                btnModifier.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");

                btnSupprimer.setOnAction(event -> {
                    Parcelle p = getTableView().getItems().get(getIndex());
                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Confirmation de suppression");
                    confirmation.setHeaderText(null);
                    confirmation.setContentText("Voulez-vous supprimer la parcelle ?");
                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            parcelleService.supprimer(p.getId());
                            data.remove(p);
                            showAlert("Suppression", "✅ Parcelle supprimée !");
                        }
                    });
                });

                btnModifier.setOnAction(event -> {
                    Parcelle p = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierParcelleBack.fxml"));
                        Stage stage = new Stage();
                        stage.setScene(new Scene(loader.load()));
                        stage.initModality(Modality.APPLICATION_MODAL);
                        ModifierParcelleBackController controller = loader.getController();
                        controller.setParcelleToModify(p);
                        stage.setOnHiding(e -> loadData());
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Impossible d'ouvrir la modification.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hBox = new HBox(10, btnModifier, btnSupprimer);
                    hBox.setPadding(new Insets(5));
                    setGraphic(hBox);
                }
            }
        });
    }

    private void ajouterColonneImage() {
        imageCol.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Parcelle parcelle = getTableView().getItems().get(getIndex());
                    try {
                        Image image = new Image(parcelle.getImage(), true);
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
