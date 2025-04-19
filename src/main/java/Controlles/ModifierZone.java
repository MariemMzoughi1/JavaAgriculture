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

        // Pré-remplir les champs avec les valeurs actuelles
        nomZoneField.setText(zone.getNom_zone());
        superficieField.setText(String.valueOf(zone.getSuperficie_zone()));
        localisationField.setText(zone.getLocalisation_zone());
    }

    @FXML
    private void enregistrerModification() {
        try {
            // Récupérer les nouvelles valeurs
            String nouveauNom = nomZoneField.getText().trim();
            float nouvelleSuperficie = Float.parseFloat(superficieField.getText().trim());
            String nouvelleLocalisation = localisationField.getText().trim();

            // Modifier l'objet zone
            zoneAModifier.setNom_zone(nouveauNom);
            zoneAModifier.setSuperficie_zone(nouvelleSuperficie);
            zoneAModifier.setLocalisation_zone(nouvelleLocalisation);

            // Mise à jour en base
            zoneService.update(zoneAModifier);

            // Message console
            System.out.println("✅ Zone modifiée avec succès : " + zoneAModifier);

            // Fermer la fenêtre
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
