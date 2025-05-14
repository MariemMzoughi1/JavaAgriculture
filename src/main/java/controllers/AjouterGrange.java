package controllers;

import entities.Grange;
import entities.Zone;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.GrangeService;
import services.ZoneService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterGrange implements Initializable {

    @FXML
    private TextField typeGrangeField;

    @FXML
    private TextField capaciteField;

    @FXML
    private ComboBox<Zone> zoneComboBox;

    @FXML
    private Label messageErreur;

    private final GrangeService grangeService = new GrangeService();
    private final ZoneService zoneService = new ZoneService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Zone> zones = zoneService.find();
        zoneComboBox.setItems(FXCollections.observableArrayList(zones));

        // Affiche le nom de la zone dans la liste déroulante
        zoneComboBox.setCellFactory(param -> new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(zone.getNom_zone()); // Affiche le nom de la zone
                }
            }
        });

        // Affiche le nom de la zone dans la cellule sélectionnée
        zoneComboBox.setButtonCell(new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(zone.getNom_zone()); // Affiche aussi le nom ici
                }
            }
        });
    }


    @FXML
    private void ajouterGrange(ActionEvent event) {
        try {
            String type = typeGrangeField.getText().trim();
            if (type.isEmpty()) {
                showAlert(AlertType.ERROR, "Erreur", "Le type de la grange est requis.");
                return;
            }

            float capacite;
            try {
                capacite = Float.parseFloat(capaciteField.getText());
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Erreur", "Capacité invalide !");
                return;
            }

            Zone selectedZone = zoneComboBox.getValue();
            if (selectedZone == null) {
                showAlert(AlertType.ERROR, "Erreur", "Veuillez sélectionner une zone.");
                return;
            }

            Grange grange = new Grange(type, capacite);
            grange.setZone(selectedZone);
            float productivite = calculerProductivite(grange);
            grange.setProductivite(productivite);

            grangeService.add(grange);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Grange ajoutée avec succès !");
            alert.showAndWait();

            // 🔄 Navigation classique vers ListeGrangesUser.fxml
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ListeGrangesUser.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Liste des Granges");
            stage.setScene(new Scene(loader.load()));
            stage.show();

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur : " + e.getMessage());
        }
    }


    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private float calculerProductivite(Grange grange) {
        String type = grange.getType_grange().toLowerCase();

        switch (type) {
            case "vache":
            case "vaches":
            case "vache1":
                return grange.getCapacite() * 20f;
            case "mouton":
            case "moutons":
            case "mouton1":
                return grange.getCapacite() * 5f;
            case "poule":
            case "poules":
            case "poule1":
                return grange.getCapacite() * 1f;
            default:
                return grange.getCapacite() * 1.2f;
        }
    }
    @FXML
    private void retourListe(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/ListeGrangesUser.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Liste des Granges");
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Impossible de retourner à la liste.");
        }
    }

}

