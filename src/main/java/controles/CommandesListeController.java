package controles;

import Entites.Commande;
import Services.CommandeService;
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

import java.io.IOException;

public class CommandesListeController {

    @FXML
    private TableView<Commande> tableCommandes;

    @FXML
    private TableColumn<Commande, String> colEtat;

    @FXML
    private TableColumn<Commande, String> colDate;

    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("datecommande"));

        ajouterColonneActions();
        chargerCommandes();
    }

    private void ajouterColonneActions() {
        TableColumn<Commande, Void> colActions = new TableColumn<>("Actions");

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnInfo = new Button("Information");
            private final Button btnDelete = new Button("Supprimer");

            {
                btnInfo.setStyle("-fx-background-color: #288edf; -fx-text-fill: white;");
                btnDelete.setStyle("-fx-background-color: #f31f10; -fx-text-fill: white;");
                btnInfo.setOnAction(event -> onInfoClicked(getTableRow().getItem()));
                btnDelete.setOnAction(event -> onDeleteClicked(getTableRow().getItem()));
            }

            private final HBox pane = new HBox(10, btnInfo, btnDelete);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableCommandes.getColumns().add(colActions);
    }

    private void chargerCommandes() {
        ObservableList<Commande> commandes = FXCollections.observableArrayList(commandeService.find());
        tableCommandes.setItems(commandes);
    }

    private void onInfoClicked(Commande commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commandeDetails.fxml"));
            Parent root = loader.load();

            CommandeDetailsController controller = loader.getController();
            controller.setCommande(commande);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de la commande");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture des détails de la commande.");
        }
    }


    private void onDeleteClicked(Commande commande) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette commande ?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                commandeService.delete(commande);
                chargerCommandes();
                showInfo("Commande supprimée avec succès.");
            }
        });
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.show();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.show();
    }
}
