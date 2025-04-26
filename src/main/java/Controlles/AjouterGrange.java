package Controlles;

import Entites.Grange;
import Entites.Zone;
import Services.GrangeService;
import Services.ZoneService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

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

    public void initialize(URL url, ResourceBundle rb) {
        List<Zone> zones = zoneService.find();
        zoneComboBox.setItems(FXCollections.observableArrayList(zones));

        zoneComboBox.setCellFactory(param -> new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(zone.getId()));
                }
            }
        });

        zoneComboBox.setButtonCell(new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(zone.getId()));
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

            // Calculer la productivité et la stocker (SEULEMENT EN CHIFFRE)
            float productivite = calculerProductivite(grange);
            grange.setProductivite(productivite);

            grangeService.add(grange);

            showAlert(AlertType.INFORMATION, "Succès", "Grange ajoutée avec succès !");
            ((Button) event.getSource()).getScene().getWindow().hide();

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

    // Nouvelle version qui retourne juste un float (pas de texte)
    private float calculerProductivite(Grange grange) {
        String type = grange.getType_grange().toLowerCase();

        switch (type) {
            case "vache":
            case "vaches":
                return grange.getCapacite() * 20f;
            case "mouton":
            case "moutons":
                return grange.getCapacite() * 5f;
            case "poule":
            case "poules":
                return grange.getCapacite() * 1f;
            default:
                return grange.getCapacite() * 1.2f;
        }
    }
}
