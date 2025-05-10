package com.esprit.Controllers;

import com.esprit.models.Culture;
import com.esprit.services.CultureService;
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

public class AfficherCultureBackController {

    @FXML private TableView<Culture> cultureTable;
    @FXML private TableColumn<Culture, Integer> idCol;
    @FXML private TableColumn<Culture, String> nomCol;
    @FXML private TableColumn<Culture, String> categorieCol;
    @FXML private TableColumn<Culture, Double> quantiteCol;
    @FXML private TableColumn<Culture, String> saisonCol;
    @FXML private TableColumn<Culture, Void> actionCol;
    @FXML private TableColumn<Culture, Void> imageCol;

    private final CultureService cultureService = new CultureService();
    private final ObservableList<Culture> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        quantiteCol.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        saisonCol.setCellValueFactory(new PropertyValueFactory<>("saison"));

        loadData();
        ajouterColonneImage();
        ajouterColonnesActions();
    }

    private void loadData() {
        data.clear();
        data.addAll(cultureService.afficher());
        cultureTable.setItems(data);
    }

    private void ajouterColonnesActions() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("✏️ Modifier");
            private final Button btnSupprimer = new Button("🗑 Supprimer");

            {
                btnModifier.setStyle(
                        "-fx-background-color: #3498db;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 12px;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 4 8 4 8;"
                );

                btnSupprimer.setStyle(
                        "-fx-background-color: #e74c3c;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 12px;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 4 8 4 8;"
                );

                btnSupprimer.setOnAction(event -> {
                    Culture culture = getTableView().getItems().get(getIndex());

                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Confirmation de suppression");
                    confirmation.setHeaderText(null);
                    confirmation.setContentText("Voulez-vous supprimer \"" + culture.getNom() + "\" ?");

                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            if (cultureService.supprimer(culture.getId())) {
                                data.remove(culture);
                                showAlert("Suppression", "✅ Culture supprimée !");
                            } else {
                                showAlert("Erreur", "❌ Échec de la suppression.");
                            }
                        }
                    });
                });

                btnModifier.setOnAction(event -> {
                    Culture culture = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCultureBack.fxml"));
                        Stage stage = new Stage();
                        stage.setScene(new Scene(loader.load()));
                        stage.initModality(Modality.APPLICATION_MODAL);

                        // ✅ Corriger ici : bon controller
                        ModifierCultureBackController controller = loader.getController();
                        controller.setCultureToModify(culture);

                        stage.setOnHiding(e -> loadData());
                        stage.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Impossible d’ouvrir le formulaire de modification.");
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
                    hBox.setPadding(new Insets(5, 0, 5, 0));
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
                    Culture culture = getTableView().getItems().get(getIndex());
                    try {
                        Image image = new Image(culture.getImage(), true);
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
