package controllers;

import entities.Commande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.CommandeService;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommandeAdminController {

    @FXML private TableView<Commande> tableCommandes;
    @FXML private TableColumn<Commande, Integer> colId;
    @FXML private TableColumn<Commande, String> colEtat;
    @FXML private TableColumn<Commande, String> colDate;
    @FXML private TableColumn<Commande, Double> colTotal;
    @FXML private TableColumn<Commande, Void> colActions;

    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cd -> new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getId()).asObject());
        colEtat.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getEtat()));
        colDate.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getDatecommande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colTotal.setCellValueFactory(cd -> new javafx.beans.property.SimpleDoubleProperty(cd.getValue().getTotal()).asObject());

        addActionButtons();

        refreshTable();
    }

    private void refreshTable() {
        List<Commande> commandes = commandeService.find();
        ObservableList<Commande> observableList = FXCollections.observableArrayList(commandes);
        tableCommandes.setItems(observableList);
    }

    private void addActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑️ Supprimer");

            {
                btnDelete.setOnAction(e -> {
                    Commande commande = getTableView().getItems().get(getIndex());
                    commandeService.delete(commande);
                    refreshTable();
                });
                btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(btnDelete);
                    hbox.setSpacing(5);
                    setGraphic(hbox);
                }
            }
        });
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/admin-dashboard-view.fxml"));
            VBox root = loader.load();
            Stage stage = (Stage) tableCommandes.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
