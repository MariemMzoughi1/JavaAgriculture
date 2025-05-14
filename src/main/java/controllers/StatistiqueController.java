package controllers;

import entities.Produit;
import services.ProduitService;
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
    @FXML
    private Button btnExporterImage;

    private boolean afficherStock = true;
    private boolean utiliserLineChart = true;

    private LineChart<String, Number> lineChart;
    private BarChart<String, Number> barChart;

    private ObservableList<XYChart.Series<String, Number>> stockData;
    private ObservableList<XYChart.Series<String, Number>> prixData;

    @FXML
    private void initialize() {
        ProduitService produitService = new ProduitService();
        List<Produit> produits = produitService.find();

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

        CategoryAxis xAxisLine = new CategoryAxis();
        NumberAxis yAxisLine = new NumberAxis();
        lineChart = new LineChart<>(xAxisLine, yAxisLine);

        CategoryAxis xAxisBar = new CategoryAxis();
        NumberAxis yAxisBar = new NumberAxis();
        barChart = new BarChart<>(xAxisBar, yAxisBar);

        lineChart.setTitle("Statistiques");
        barChart.setTitle("Statistiques");

        lineChart.getStylesheets().add(getClass().getResource("/com/example/projectjava/chart-style.css").toExternalForm());
        barChart.getStylesheets().add(getClass().getResource("/com/example/projectjava/chart-style.css").toExternalForm());

        // Par défaut : LineChart avec Stock
        lineChart.setData(stockData);
        chartContainer.getChildren().add(lineChart);
        ajouterLabels(lineChart); // <<=== ajouter les labels tout de suite

        btnSwitcherData.setOnAction(e -> switchData());
        btnSwitcherChartType.setOnAction(e -> switchChartType());

    }


    private void switchData() {
        afficherStock = !afficherStock;

        if (utiliserLineChart) {
            lineChart.setData(afficherStock ? stockData : prixData);
            ajouterLabels(lineChart);
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
            ajouterLabels(lineChart);
        } else {
            barChart.setData(afficherStock ? stockData : prixData);
            chartContainer.getChildren().add(barChart);
        }

        btnSwitcherChartType.setText(utiliserLineChart ? "Passer en BarChart" : "Passer en LineChart");
    }

    private void ajouterLabels(LineChart<String, Number> chart) {
        for (XYChart.Series<String, Number> series : chart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                // Nettoyer l'ancien label s'il existe
                StackPane stackPane = (StackPane) data.getNode();
                stackPane.getChildren().clear();

                // Créer un nouveau label
                javafx.scene.control.Label label = new javafx.scene.control.Label(
                        data.getXValue() + " : " + data.getYValue()
                );
                label.setStyle("-fx-font-size: 10px; -fx-text-fill: black; -fx-font-weight: bold;");

                stackPane.getChildren().add(label);
            }
        }
    }


}
