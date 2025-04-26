package controles;

import Entites.Produit;
import Services.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.util.List;

public class StatistiqueController {

    @FXML
    private StackPane chartContainer; // On met le chart ici (LineChart ou BarChart)

    @FXML
    private Button btnSwitcherData;
    @FXML
    private Button btnSwitcherChartType;

    private boolean afficherStock = true;
    private boolean utiliserLineChart = true;

    private LineChart<String, Number> lineChart;
    private BarChart<String, Number> barChart;

    private ObservableList<XYChart.Series<String, Number>> stockData;
    private ObservableList<XYChart.Series<String, Number>> prixData;

    @FXML
    private void initialize() {
        // Charger les produits
        ProduitService produitService = new ProduitService();
        List<Produit> produits = produitService.find();

        // Préparer les données
        stockData = FXCollections.observableArrayList();
        prixData = FXCollections.observableArrayList();

        XYChart.Series<String, Number> stockSeries = new XYChart.Series<>();
        stockSeries.setName("Stock");

        XYChart.Series<String, Number> prixSeries = new XYChart.Series<>();
        prixSeries.setName("Prix");

        for (Produit p : produits) {
            stockSeries.getData().add(new XYChart.Data<>(p.getNom(), p.getQuantite_stock()));
            prixSeries.getData().add(new XYChart.Data<>(p.getNom(), p.getPrix_unitaire()));
        }

        stockData.add(stockSeries);
        prixData.add(prixSeries);

        // Initialiser LineChart et BarChart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        lineChart = new LineChart<>(xAxis, yAxis);
        barChart = new BarChart<>(xAxis, yAxis);

        lineChart.setTitle("Statistiques");
        barChart.setTitle("Statistiques");

        // Appliquer le style
        lineChart.getStylesheets().add(getClass().getResource("/chart-style.css").toExternalForm());
        barChart.getStylesheets().add(getClass().getResource("/chart-style.css").toExternalForm());

        // Par défaut : LineChart avec Stock
        lineChart.setData(stockData);
        chartContainer.getChildren().add(lineChart);

        // Boutons action
        btnSwitcherData.setOnAction(e -> switchData());
        btnSwitcherChartType.setOnAction(e -> switchChartType());
    }

    private void switchData() {
        afficherStock = !afficherStock;

        if (utiliserLineChart) {
            lineChart.setData(afficherStock ? stockData : prixData);
        } else {
            barChart.setData(afficherStock ? stockData : prixData);
        }

        btnSwitcherData.setText(afficherStock ? "Afficher Prix" : "Afficher Stock");
    }

    private void switchChartType() {
        utiliserLineChart = !utiliserLineChart;

        chartContainer.getChildren().clear();

        if (utiliserLineChart) {
            lineChart.setData(afficherStock ? stockData : prixData);
            chartContainer.getChildren().add(lineChart);
        } else {
            barChart.setData(afficherStock ? stockData : prixData);
            chartContainer.getChildren().add(barChart);
        }

        btnSwitcherChartType.setText(utiliserLineChart ? "Passer en BarChart" : "Passer en LineChart");
    }
}
