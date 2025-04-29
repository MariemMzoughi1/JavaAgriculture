package Controlles;

import Entites.Grange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.stage.Stage;
import Services.GrangeService;

import java.io.IOException;

public class ListeGrangesUser {

    @FXML private TableView<Grange> tableGranges;
    @FXML private TableColumn<Grange, String> colType;
    @FXML private TableColumn<Grange, Float> colCapacite;
    @FXML private TableColumn<Grange, Float> colProductivite;
    @FXML private TableColumn<Grange, Integer> colZone;
    @FXML private TextField searchField;

    private ObservableList<Grange> grangesList;
    private final GrangeService grangeService = new GrangeService();

    @FXML
    public void initialize() {
        grangesList = FXCollections.observableArrayList(grangeService.find());

        colType.setCellValueFactory(new PropertyValueFactory<>("type_grange"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colProductivite.setCellValueFactory(new PropertyValueFactory<>("productivite"));
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
                    setText(String.format("%.2f - %s", item, niveau));
                }
            }
        });

        tableGranges.setItems(grangesList);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterGranges(newValue);
        });
    }

    @FXML
    public void trierParType() {
        grangesList.sort((grange1, grange2) -> grange1.getType_grange().compareTo(grange2.getType_grange()));
    }

    @FXML
    public void trierParCapacite() {
        grangesList.sort((grange1, grange2) -> Float.compare(grange1.getCapacite(), grange2.getCapacite()));
    }

    @FXML
    public void trierParProductivite() {
        grangesList.sort((grange1, grange2) -> Float.compare(grange1.getProductivite(), grange2.getProductivite()));
    }

    private void filterGranges(String searchText) {
        ObservableList<Grange> filtered = FXCollections.observableArrayList();
        for (Grange g : grangesList) {
            if (g.getType_grange().toLowerCase().contains(searchText.toLowerCase())) {
                filtered.add(g);
            }
        }
        tableGranges.setItems(filtered);
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
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
