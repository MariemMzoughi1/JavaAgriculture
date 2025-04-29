package Controlles;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class AdminDashboard {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private void consulterZones(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/ListeZones.fxml"));
        contentPane.getChildren().setAll(pane);
    }

    @FXML
    private void consulterGranges(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(getClass().getResource("/ListeGranges.fxml"));
        contentPane.getChildren().setAll(pane);
    }
}
