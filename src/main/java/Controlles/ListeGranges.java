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
    @FXML private TableColumn<Grange, Float> colProductivite;
    @FXML private TableColumn<Grange, Integer> colZone;
    @FXML private TableColumn<Grange, Void> colActions;
    @FXML private TextField searchField; // Champ de recherche

    private final GrangeService grangeService = new GrangeService();
    private final ObservableList<Grange> granges = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colType.setCellValueFactory(new PropertyValueFactory<>("type_grange"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colProductivite.setCellValueFactory(new PropertyValueFactory<>("productivite")); // OK float direct
        colZone.setCellValueFactory(cellData -> {
            if (cellData.getValue().getZone() != null) {
                return new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getZone().getId()).asObject();
            } else {
                return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
            }
        });

        colProductivite.setCellFactory(param -> new TableCell<Grange, Float>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String niveau;
                    if (item < 20) {
                        niveau = "Faible";
                        setStyle("-fx-text-fill: red;");
                    } else if (item >= 20 && item <= 50) {
                        niveau = "Moyenne";
                        setStyle("-fx-text-fill: orange;");
                    } else {
                        niveau = "Élevée";
                        setStyle("-fx-text-fill: green;");
                    }

                    // Afficher le chiffre + niveau
                    setText(String.format("%.2f - %s", item, niveau));
                }
            }
        });



        granges.addAll(grangeService.find());
        tableGranges.setItems(granges);
        addActionButtonsToTable();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterGranges(newValue);
        });
    }



    private void filterGranges(String searchText) {
        ObservableList<Grange> filtered = FXCollections.observableArrayList();

        for (Grange g : grangeService.find()) {
            if (g.getType_grange().toLowerCase().contains(searchText.toLowerCase())) {
                filtered.add(g);
            }
        }

        tableGranges.setItems(filtered);
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<Grange, Void>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnModifier.setOnAction(event -> {
                    Grange grange = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierGrange.fxml"));
                        Scene scene = new Scene(loader.load());

                        ModifierGrange controller = loader.getController();
                        controller.setGrange(grange);

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Grange");
                        stage.setScene(scene);
                        stage.showAndWait();

                        getTableView().refresh();

                    } catch (IOException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la fenêtre de modification : " + e.getMessage());
                    }
                });

                btnSupprimer.setOnAction(event -> {
                    Grange grange = getTableView().getItems().get(getIndex());
                    grangeService.delete(grange);
                    granges.remove(grange);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Grange supprimée avec succès !");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox pane = new HBox(10, btnModifier, btnSupprimer);
                    setGraphic(pane);
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
    @FXML
    private void ouvrirChartProductivite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/productivite_chart.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Graphique de Productivité");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType); // Utiliser le type passé en paramètre
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
