package Controlles;

import Entites.Grange;
import Services.GrangeService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductiviteChart implements Initializable {

    @FXML
    private BarChart<String, Number> productiviteChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final GrangeService grangeService = new GrangeService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerDonnees();
    }

    private void chargerDonnees() {
        List<Grange> granges = grangeService.find();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Productivité des granges");

        for (Grange grange : granges) {
            float productivite = grange.getProductivite();
            String niveau = getNiveauProductivite(productivite);

            // Ex: "Vaches (Élevée)" sur l'axe X
            String label = grange.getType_grange() + " (" + niveau + ")";

            serie.getData().add(new XYChart.Data<>(label, productivite));
        }

        productiviteChart.getData().add(serie);
    }

    private String getNiveauProductivite(float productivite) {
        if (productivite < 20) {
            return "Faible";
        } else if (productivite >= 20 && productivite <= 50) {
            return "Moyenne";
        } else {
            return "Élevée";
        }
    }
}
