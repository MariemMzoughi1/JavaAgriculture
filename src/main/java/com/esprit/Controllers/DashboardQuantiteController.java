package com.esprit.Controllers;

import com.esprit.services.CultureService;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

import java.util.Map;

public class DashboardQuantiteController {

    @FXML private Label titreLabel;
    @FXML private PieChart quantiteParCategorieChart;

    private final CultureService cultureService = new CultureService();

    @FXML
    public void initialize() {
        afficherStatistiques();
    }

    public void afficherStatistiques() {
        Map<String, Double> quantites = cultureService.getQuantitesParCategorie();

        quantiteParCategorieChart.getData().clear();

        int index = 0;
        String[] couleurs = {"#00bfff", "#f39c12", "#e74c3c", "#c0392b", "#9b59b6", "#1abc9c", "#ff6f61"};

        for (Map.Entry<String, Double> entry : quantites.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
            quantiteParCategorieChart.getData().add(data);
        }

        // Appliquer les couleurs après que les nœuds soient ajoutés
        int colorIndex = 0;
        for (PieChart.Data data : quantiteParCategorieChart.getData()) {
            String color = couleurs[colorIndex % couleurs.length];
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            colorIndex++;
        }

        // 🎞️ Animation d'apparition
        for (PieChart.Data data : quantiteParCategorieChart.getData()) {
            data.getNode().setScaleX(0);
            data.getNode().setScaleY(0);

            ScaleTransition st = new ScaleTransition(Duration.millis(700), data.getNode());
            st.setToX(1);
            st.setToY(1);
            st.play();
        }

        titreLabel.setText("📊 Répartition des quantités de cultures par catégorie");
    }
}