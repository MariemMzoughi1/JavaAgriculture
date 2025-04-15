package Controlles;

import Entites.Zone;
import Services.ZoneService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifierZone {

    @FXML
    private TextField nomZoneField;

    @FXML
    private TextField superficieField;

    @FXML
    private TextField localisationField;

    private Zone zoneAModifier;
    private final ZoneService zoneService = new ZoneService(); // 🔥 Instanciation du service

    public void setZone(Zone zone) {
        this.zoneAModifier = zone;


        nomZoneField.setText(zone.getNom_zone());
        superficieField.setText(String.valueOf(zone.getSuperficie_zone()));
        localisationField.setText(zone.getLocalisation_zone());
    }

    @FXML
    private void enregistrerModification() {

        zoneAModifier.setNom_zone(nomZoneField.getText());
        zoneAModifier.setSuperficie_zone(Float.parseFloat(superficieField.getText()));
        zoneAModifier.setLocalisation_zone(localisationField.getText());


        zoneService.update(zoneAModifier);


        System.out.println("✅ Zone modifiée avec succès : " + zoneAModifier);


        Stage stage = (Stage) nomZoneField.getScene().getWindow();
        stage.close();
    }
}
