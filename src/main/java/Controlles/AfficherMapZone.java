package Controlles;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class AfficherMapZone {

    @FXML
    private WebView mapView;

    private double latitude;
    private double longitude;

    // Méthode pour initialiser la carte et afficher la localisation
    @FXML
    public void initialize() {
        WebEngine webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/mapbox.html").toExternalForm());

        // Ajouter un listener pour l'événement de chargement
        webEngine.getLoadWorker().stateProperty().addListener((obs, old, newV) -> {
            if (newV == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
            }
        });
    }

    // Setter pour les coordonnées (latitude, longitude)
    public void setCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;

        // Exécuter un script pour centrer la carte sur la position
        WebEngine webEngine = mapView.getEngine();
        String script = String.format(
                "map.setCenter([%f, %f]); map.setZoom(12);", latitude, longitude
        );
        webEngine.executeScript(script);
    }

    // Classe JavaConnector pour la communication avec JavaScript
    public class JavaConnector {
        public void sendCoordinates(double lng, double lat) {
            // Peut être utilisé si tu veux envoyer des coordonnées
        }
    }
}
