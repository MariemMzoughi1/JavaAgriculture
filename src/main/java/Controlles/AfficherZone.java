package Controlles;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.io.IOException;

import Entites.Grange;
import Entites.Zone;
import Services.GrangeService;
import Services.ZoneService;

import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class AfficherZone {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField localisation;

    @FXML
    private TextField nomdezone;

    @FXML
    private TextField superficie;

    @FXML
    private WebView mapView;

    @FXML
    private ImageView imageView;

    @FXML
    private VBox grangesList;

    @FXML
    private Button btnProposerCulture;

    private int currentZoneId; // ID de la zone actuellement affichée

    public void setSuperficie(float superficie) {
        this.superficie.setText(Float.toString(superficie));
    }

    public void setNomdezone(String nomdezone) {
        this.nomdezone.setText(nomdezone);
    }

    public void setLocalisation(String localisation) {
        this.localisation.setText(localisation);
    }

    public void setImage(String image) {
        if (image != null && !image.isEmpty()) {
            Image img = new Image("file:" + image);
            imageView.setImage(img);
        }
    }

    @FXML
    void initialize() {
        // Initialisation si nécessaire
    }

    @FXML
    private void retourListeZones(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeZones.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Liste des zones");
        stage.show();
    }

    public void setZone(Zone zone) {
        this.currentZoneId = zone.getId(); // Stocker l'ID de la zone
        setNomdezone(zone.getNom_zone());
        setSuperficie(zone.getSuperficie_zone());
        setLocalisation(zone.getLocalisation_zone());
        setImage(zone.getImage());
        afficherGranges(zone.getId());
        afficherZoneAvecCarte(zone.getId());
    }

    public void afficherZoneAvecCarte(int zoneId) {
        ZoneService zoneService = new ZoneService();
        Zone zone = zoneService.findById(zoneId);

        if (zone != null) {
            String localisation = zone.getLocalisation_zone();
            afficherCarte(localisation);
        } else {
            System.out.println("Zone non trouvée.");
        }
    }

    private void afficherCarte(String localisation) {
        if (localisation == null || localisation.isEmpty() || !localisation.contains(",")) {
            System.out.println("Localisation invalide.");
            return;
        }

        String[] parts = localisation.split(",");
        String latitude = parts[0].trim();
        String longitude = parts[1].trim();

        WebEngine webEngine = mapView.getEngine();
        URL mapFileUrl = getClass().getResource("/mapbox.html");
        webEngine.load(mapFileUrl.toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                String script = String.format("if (window.javaConnector) { window.javaConnector.sendCoordinates(%s, %s); }", longitude, latitude);
                webEngine.executeScript(script);
            }
        });
    }

    private void afficherGranges(int zoneId) {
        GrangeService grangeService = new GrangeService();
        List<Grange> granges = grangeService.find();

        List<Grange> grangesPourZone = new ArrayList<>();
        for (Grange grange : granges) {
            if (grange.getZone().getId() == zoneId) {
                grangesPourZone.add(grange);
            }
        }

        grangesList.getChildren().clear();

        if (!grangesPourZone.isEmpty()) {
            for (Grange grange : grangesPourZone) {
                Label grangeLabel = new Label("Grange: " + grange.getType_grange() + " - Capacité: " + grange.getCapacite() + " - Productivité: " + grange.getProductivite());
                grangesList.getChildren().add(grangeLabel);
            }
        } else {
            Label noGrangesLabel = new Label("Aucune grange associée à cette zone.");
            grangesList.getChildren().add(noGrangesLabel);
        }
    }

    @FXML
    private void afficherProductivite(ActionEvent event) throws IOException {
        int zoneId = currentZoneId;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProductiviteChartZone.fxml"));
        Parent root = loader.load();

        ProductiviteChartZone controller = loader.getController();
        controller.setZoneId(zoneId);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Productivité des granges");
        stage.show();
    }

    @FXML
    private void proposerCulture(ActionEvent event) {
        String superficieText = superficie.getText();

        if (superficieText == null || superficieText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez saisir une superficie valide.");
            alert.showAndWait();
            return;
        }

        double superficieZone;
        try {
            superficieZone = Double.parseDouble(superficieText);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de format");
            alert.setHeaderText(null);
            alert.setContentText("La superficie doit être un nombre !");
            alert.showAndWait();
            return;
        }

        Stage stage = new Stage();
        stage.setTitle("Cultures proposées");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("Cultures recommandées");
        titre.setStyle("-fx-font-size: 20px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");

        VBox culturesBox = new VBox(10);
        culturesBox.setAlignment(Pos.CENTER);

        if (superficieZone < 500) {
            culturesBox.getChildren().addAll(
                    new Label("- Tomates"),
                    new Label("- Fraises"),
                    new Label("- Salades")
            );
        } else if (superficieZone >= 500 && superficieZone < 2000) {
            culturesBox.getChildren().addAll(
                    new Label("- Orangers"),
                    new Label("- Pêchers")
            );
        } else {
            culturesBox.getChildren().addAll(
                    new Label("- Oliviers"),
                    new Label("- Vignes"),
                    new Label("- Amandiers")
            );
        }

        Button fermerButton = new Button("Fermer");
        fermerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        fermerButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(titre, culturesBox, fermerButton);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
}