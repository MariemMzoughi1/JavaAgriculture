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

import java.io.IOException;

public class ListeGranges {

    @FXML private TableView<Grange> tableGranges;
    @FXML private TableColumn<Grange, String> colType;
    @FXML private TableColumn<Grange, Float> colCapacite;
    @FXML private TableColumn<Grange, Void> colActions;

    private final ObservableList<Grange> granges = FXCollections.observableArrayList(
            new Grange("Grange bovins", 50),
            new Grange("Grange ovins", 30)
    );

    @FXML
    public void initialize() {
        colType.setCellValueFactory(new PropertyValueFactory<>("type_grange"));
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));

        tableGranges.setItems(granges);
        addActionButtonsToTable();
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnModifier.setOnAction(event -> {
                    Grange grange = getTableView().getItems().get(getIndex());
                    System.out.println("Modifier : " + grange);
                    // TODO: ouvrir interface de modification
                });

                btnSupprimer.setOnAction(event -> {
                    Grange grange = getTableView().getItems().get(getIndex());
                    granges.remove(grange);
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
}
