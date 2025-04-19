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
        List<Zone> zones = zoneService.find(); // récupère les zones depuis la BDD
        zoneComboBox.setItems(FXCollections.observableArrayList(zones)); // les injecte dans le ComboBox

        // CellFactory pour afficher l'ID de la zone dans le ComboBox
        zoneComboBox.setCellFactory(param -> new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(zone.getId())); // Affiche uniquement l'ID
                }
            }
        });

        // Affichage de l'ID de la zone dans la liste déroulante
        zoneComboBox.setButtonCell(new ListCell<Zone>() {
            @Override
            protected void updateItem(Zone zone, boolean empty) {
                super.updateItem(zone, empty);
                if (empty || zone == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(zone.getId())); // Affiche uniquement l'ID
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

            grangeService.add(grange);

            // Alerte de succès
            showAlert(AlertType.INFORMATION, "Succès", "Grange ajoutée avec succès !");
        } catch (Exception e) {
            // Alerte en cas d'exception
            showAlert(AlertType.ERROR, "Erreur", "Erreur : " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Pas de texte en en-tête
        alert.setContentText(message);
        alert.showAndWait(); // Affiche l'alerte et attend que l'utilisateur la ferme
    }


}
