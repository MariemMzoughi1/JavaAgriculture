package Controlles;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class AdminDashboard {

    @FXML
    private AnchorPane contentPane;

    // Méthode générique pour charger une vue dans contentPane
    private void chargerVue(String cheminFXML) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource(cheminFXML));
            contentPane.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace(); // ou afficher une alerte si nécessaire
        }
    }

    @FXML
    private void consulterZones(ActionEvent event) {
        chargerVue("/ListeZones.fxml");
    }

    @FXML
    private void consulterGranges(ActionEvent event) {
        chargerVue("/ListeGranges.fxml");
    }
}
