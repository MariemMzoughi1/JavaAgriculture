package controllers;

import entities.Grange;
import entities.Zone;
import services.GrangeService;
import services.ZoneService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierGrange implements Initializable {

    @FXML private TextField typeGrangeField;
    @FXML private TextField capaciteField;
    @FXML private ComboBox<Zone> zoneComboBox;
    @FXML private Label messageErreur;

    private Grange grange; // grange à modifier
    private final GrangeService grangeService = new GrangeService();
    private final ZoneService zoneService = new ZoneService();

    public void setGrange(Grange grange) {
        this.grange = grange;
        typeGrangeField.setText(grange.getType_grange());
        capaciteField.setText(String.valueOf(grange.getCapacite()));
        zoneComboBox.setValue(grange.getZone());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Zone> zones = zoneService.find();
        zoneComboBox.setItems(FXCollections.observableArrayList(zones));
    }

    @FXML
    private void modifierGrange() {
        try {
            String type = typeGrangeField.getText().trim();
            float capacite = Float.parseFloat(capaciteField.getText());
            Zone zone = zoneComboBox.getValue();

            if (type.isEmpty() || zone == null) {
                messageErreur.setText("Tous les champs sont requis.");
                return;
            }

            grange.setType_grange(type);
            grange.setCapacite(capacite);
            grange.setZone(zone);

            grangeService.update(grange);
            messageErreur.setStyle("-fx-text-fill: green;");
            messageErreur.setText("Grange modifiée avec succès.");

            fermerFenetre();
        } catch (NumberFormatException e) {
            messageErreur.setText("Capacité invalide !");
        } catch (Exception e) {
            messageErreur.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) typeGrangeField.getScene().getWindow();
        stage.close();
    }
}
