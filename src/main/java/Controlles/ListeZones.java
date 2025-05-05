package Controlles;

import Entites.Zone;
import Services.ZoneService;
import Utils.ExportCSV;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListeZones implements Initializable {

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnRetour;

    @FXML
    private VBox zoneContainer;

    @FXML
    private javafx.scene.control.TextField searchField;


    private ZoneService zoneService = new ZoneService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Zone> zones = zoneService.find();
        for (Zone zone : zones) {
            HBox box = createZoneBox(zone);
            zoneContainer.getChildren().add(box);
        }

        // 🔍 Ajout de l'écouteur de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            rechercherZones();
        });
    }


    private HBox createZoneBox(Zone zone) {
        HBox box = new HBox(10);
        box.setStyle("-fx-padding: 10px; -fx-background-color: #ffffff; -fx-border-color: #c8e6c9;");
        box.setAlignment(Pos.CENTER_LEFT);

        Text nom = new Text("Nom: " + zone.getNom_zone());
        Text superficie = new Text("Superficie: " + zone.getSuperficie_zone() + " ha");
        Text localisation = new Text("Localisation: " + zone.getLocalisation_zone());

        Button modifierBtn = new Button("Modifier");
        modifierBtn.setStyle("-fx-background-color: #42a5f5; -fx-text-fill: white;");
        modifierBtn.setOnAction(e -> modifierZone(zone));

        Button supprimerBtn = new Button("Supprimer");
        supprimerBtn.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white;");
        supprimerBtn.setOnAction(e -> supprimerZone(zone, box));

        // ✅ Nouveau bouton "Consulter"
        Button consulterBtn = new Button("Consulter");
        consulterBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");
        consulterBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherZone.fxml"));
                Parent root = loader.load();

                // Transfert de la zone au contrôleur AfficherZone
                Controlles.AfficherZone controller = loader.getController();
                controller.setZone(zone);  // Passer la zone au contrôleur

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Détails de la Zone");
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        box.getChildren().addAll(nom, superficie, localisation, modifierBtn, supprimerBtn, consulterBtn);
        return box;
    }



    @FXML
    void ajouterZone(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterZone.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setTitle("Ajouter une Grange");
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    private void modifierZone(Zone zone) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierZone.fxml"));
            Parent root = loader.load();

            ModifierZone controller = loader.getController();
            controller.setZone(zone);

            Stage stage = new Stage();
            stage.setTitle("Modifier une Zone");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void supprimerZone(Zone zone, HBox box) {
        try {
            zoneService.delete(zone);
            zoneContainer.getChildren().remove(box);
            showAlert("Zone supprimée", "La zone a été supprimée avec succès.");
        } catch (RuntimeException e) {
            showAlert("Erreur de suppression", e.getMessage());
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void rechercherZones() {
        String keyword = searchField.getText().toLowerCase();
        List<Zone> zones = zoneService.find();

        zoneContainer.getChildren().clear();

        for (Zone zone : zones) {
            if (zone.getNom_zone().toLowerCase().contains(keyword) ||
                    zone.getLocalisation_zone().toLowerCase().contains(keyword) ||
                    String.valueOf(zone.getSuperficie_zone()).contains(keyword)) {

                HBox box = createZoneBox(zone);
                zoneContainer.getChildren().add(box);
            }
        }
    }

    @FXML
    private void trierParNom(ActionEvent event) {
        List<Zone> zonesTriees = zoneService.find().stream()
                .sorted(Comparator.comparing(Zone::getNom_zone))
                .collect(Collectors.toList());

        zoneContainer.getChildren().clear();
        zonesTriees.forEach(zone -> {
            HBox box = createZoneBox(zone);
            zoneContainer.getChildren().add(box);
        });
    }

    @FXML
    private void trierParSuperficie(ActionEvent event) {
        List<Zone> zonesTriees = zoneService.find().stream()
                .sorted(Comparator.comparingDouble(Zone::getSuperficie_zone))
                .collect(Collectors.toList());

        zoneContainer.getChildren().clear();
        zonesTriees.forEach(zone -> {
            HBox box = createZoneBox(zone);
            zoneContainer.getChildren().add(box);
        });
    }

    @FXML
    private void exporterCSV(ActionEvent event) {
        List<Zone> zones = zoneService.find();
        ExportCSV.exportZonesToCSV(zones);
    }
    @FXML
    private void exporterPDF(ActionEvent event) {
        List<Zone> zones = zoneService.find();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.setInitialFileName("zones.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));

        File selectedFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            try {
                utils.ExportPDF.exporterZonesEnPDF(zones, selectedFile.getAbsolutePath());
                showAlert("Succès", "Exportation PDF réussie !");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'exportation : " + e.getMessage());
            }
        }
    }

}
