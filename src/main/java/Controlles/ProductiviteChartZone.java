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
import java.util.stream.Collectors;

public class ProductiviteChartZone implements Initializable {

    @FXML
    private BarChart<String, Number> productiviteChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final GrangeService grangeService = new GrangeService();

    private int zoneId; // L'ID de la zone qu'on va recevoir

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // On ne charge pas tout de suite car on doit recevoir l'ID d'abord
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
        chargerDonnees(); // Charger les données après avoir reçu l'ID
    }

    private void chargerDonnees() {
        List<Grange> toutesLesGranges = grangeService.find();

        // Filtrer uniquement les granges de la zone concernée
        List<Grange> grangesPourZone = toutesLesGranges.stream()
                .filter(grange -> grange.getZone() != null && grange.getZone().getId() == zoneId)
                .collect(Collectors.toList());

        if (grangesPourZone.isEmpty()) {
            System.out.println("Aucune grange trouvée pour cette zone.");
            return;
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Productivité des granges de la zone");

        for (Grange grange : grangesPourZone) {
            float productivite = grange.getProductivite();
            String niveau = getNiveauProductivite(productivite);

            String label = grange.getType_grange() + " (" + niveau + ")";
            serie.getData().add(new XYChart.Data<>(label, productivite));
        }

        productiviteChart.getData().clear(); // Vider avant d'ajouter une nouvelle série
        productiviteChart.getData().add(serie);
    }

    private String getNiveauProductivite(float productivite) {
        if (productivite < 20) {
            return "Faible";
        } else if (productivite <= 50) {
            return "Moyenne";
        } else {
            return "Élevée";
        }
    }
}
