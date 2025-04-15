package Controlles;

import Entites.Zone;
import Services.ZoneService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeZones implements Initializable {

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnRetour;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;



    @FXML
    private TableColumn<Zone, String> nomZoneColumn;

    @FXML
    private TableColumn<Zone, String> superficieZoneColumn;

    @FXML
    private TableColumn<Zone, String> localisationZoneColumn;

    @FXML
    private TableView<Zone> zoneTable;

    private ZoneService zoneService = new ZoneService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        superficieZoneColumn.setCellValueFactory(new PropertyValueFactory<>("superficie_zone"));
        nomZoneColumn.setCellValueFactory(new PropertyValueFactory<>("nom_zone"));
        localisationZoneColumn.setCellValueFactory(new PropertyValueFactory<>("localisation_zone"));


        List<Zone> zones = zoneService.find();
        ObservableList<Zone> observableList = FXCollections.observableArrayList(zones);
        zoneTable.setItems(observableList);
    }


    @FXML
    private void ajouterZone(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterZone.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajouter une zone");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void modifierZone() {
        Zone zoneSelectionnee = zoneTable.getSelectionModel().getSelectedItem();

        if (zoneSelectionnee != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierZone.fxml"));
                Parent root = loader.load();

                ModifierZone controller = loader.getController();
                controller.setZone(zoneSelectionnee);

                Stage stage = new Stage();
                stage.setTitle("Modifier une Zone");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucune sélection");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une zone à modifier.");
            alert.showAndWait();
        }
    }


    @FXML
    private void supprimerZone() {
        Zone selectedZone = zoneTable.getSelectionModel().getSelectedItem();
        if (selectedZone != null) {
            zoneService.delete(selectedZone);
            zoneTable.getItems().remove(selectedZone);
            showAlert("Zone supprimée", "La zone a été supprimée avec succès.");
        } else {
            showAlert("Aucune zone sélectionnée", "Veuillez sélectionner une zone à supprimer.");
        }
    }

    @FXML
    private void retourAccueil(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Acceuil.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
