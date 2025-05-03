package Controlles;

import Entites.Zone;
import Services.ZoneService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    private final ZoneService zoneService = new ZoneService();

    public void setZone(Zone zone) {
        this.zoneAModifier = zone;


        nomZoneField.setText(zone.getNom_zone());
        superficieField.setText(String.valueOf(zone.getSuperficie_zone()));
        localisationField.setText(zone.getLocalisation_zone());
    }

    @FXML
    private void enregistrerModification() {
        try {

            String nouveauNom = nomZoneField.getText().trim();
            float nouvelleSuperficie = Float.parseFloat(superficieField.getText().trim());
            String nouvelleLocalisation = localisationField.getText().trim();


            zoneAModifier.setNom_zone(nouveauNom);
            zoneAModifier.setSuperficie_zone(nouvelleSuperficie);
            zoneAModifier.setLocalisation_zone(nouvelleLocalisation);


            zoneService.update(zoneAModifier);


            System.out.println("✅ Zone modifiée avec succès : " + zoneAModifier);


            Stage stage = (Stage) nomZoneField.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert("Erreur de saisie", "La superficie doit être un nombre valide.");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la modification.");
            e.printStackTrace();
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
