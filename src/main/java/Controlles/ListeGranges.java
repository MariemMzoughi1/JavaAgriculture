package Controlles;

import Entites.Grange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Services.GrangeService;


import java.io.IOException;

public class ListeGranges {

    @FXML private TableView<Grange> tableGranges;
    @FXML private TableColumn<Grange, String> colType;
    @FXML private TableColumn<Grange, Float> colCapacite;
    @FXML private TableColumn<Grange, Integer> colZone;
    @FXML private TableColumn<Grange, Void> colActions;

    private final GrangeService grangeService = new GrangeService();
    private final ObservableList<Grange> granges = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        colType.setCellValueFactory(new PropertyValueFactory<>("type_grange"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colZone.setCellValueFactory(cellData -> {
            if (cellData.getValue().getZone() != null) {
                return new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getZone().getId()).asObject();
            } else {
                return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
            }
        });

        granges.addAll(grangeService.find());
        tableGranges.setItems(granges);
        addActionButtonsToTable();
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<Grange, Void>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                // Style des boutons
                btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                // Action pour le bouton Modifier
                btnModifier.setOnAction(event -> {
                    Grange grange = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierGrange.fxml"));
                        Scene scene = new Scene(loader.load());

                        ModifierGrange controller = loader.getController();
                        controller.setGrange(grange); // passer l'objet grange à modifier

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Grange");
                        stage.setScene(scene);
                        stage.showAndWait();

                        getTableView().refresh();


                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la fenêtre de modification : " + e.getMessage());
                    }
                });

                // Action pour le bouton Supprimer
                btnSupprimer.setOnAction(event -> {
                    Grange grange = getTableView().getItems().get(getIndex());
                    grangeService.delete(grange); // Supprimer la grange via le service
                    granges.remove(grange); // Retirer la grange de la liste affichée
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Grange supprimée avec succès !");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Ne rien afficher si la ligne est vide
                } else {
                    HBox pane = new HBox(10, btnModifier, btnSupprimer);
                    setGraphic(pane); // Afficher les boutons dans la cellule
                }
            }
        });
    }


    @FXML
    void retourAccueil(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Acceuil.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void ajouterGrange(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterGrange.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setTitle("Ajouter une Grange");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType information, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
