package Controlles;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

public class AjouterMapZone {

    @FXML
    private WebView mapView;

    private AjouterZone ajouterZoneController; // Pour stocker la référence

    public void setAjouterZoneController(AjouterZone controller) {
        this.ajouterZoneController = controller;
    }

    @FXML
    public void initialize() {
        WebEngine webEngine = mapView.getEngine();
        webEngine.load(getClass().getResource("/mapbox.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((obs, old, newV) -> {
            if (newV == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", new JavaConnector());
            }
        });
    }

    public class JavaConnector {
        public void sendCoordinates(double lng, double lat) {
            if (ajouterZoneController != null) {
                ajouterZoneController.setCoordinates(lat, lng);
                Stage stage = (Stage) mapView.getScene().getWindow();
                stage.close();
            }
        }
    }
}
