package controllers;

import services.MeteoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MeteoController {

    @FXML private HBox weatherContainer;
    @FXML private ComboBox<String> regionComboBox;

    private final MeteoService meteoService = new MeteoService();
    private String regionSelectionnee = "Tunis";

    private static final String[] REGIONS_TUNISIE = {
            "Ariana", "Béja", "Ben Arous", "Bizerte", "Gabès", "Gafsa", "Jendouba", "Kairouan",
            "Kasserine", "Kebili", "Kef", "Mahdia", "Manouba", "Médenine", "Monastir", "Nabeul",
            "Sfax", "Sidi Bouzid", "Siliana", "Sousse", "Tataouine", "Tozeur", "Tunis", "Zaghouan"
    };

    @FXML
    public void initialize() {
        regionComboBox.getItems().addAll(REGIONS_TUNISIE);
        regionComboBox.setValue(regionSelectionnee);
        afficherMeteo7Jours();
    }

    @FXML
    private void changerRegion() {
        regionSelectionnee = regionComboBox.getValue();
        rafraichirMeteo();
    }

    @FXML
    private void rafraichirMeteo() {
        afficherMeteo7Jours();
    }

    private void afficherMeteo7Jours() {
        String jsonData = meteoService.getMeteoData(regionSelectionnee);

        if (jsonData == null) {
            weatherContainer.getChildren().setAll(new Label("Erreur lors de la récupération des données météo."));
            return;
        }

        JsonObject root = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonArray list = root.getAsJsonArray("list");

        weatherContainer.getChildren().clear();

        int nbMax = Math.min(7, list.size() / 8);

        for (int i = 0; i < nbMax; i++) {
            int index = i * 8;
            if (index >= list.size()) break;

            JsonObject forecast = list.get(index).getAsJsonObject();

            String dateComplete = forecast.get("dt_txt").getAsString();
            String dateJour = dateComplete.split(" ")[0];
            String jourSemaine = getDayOfWeek(dateJour);
            double temp = forecast.getAsJsonObject("main").get("temp").getAsDouble();
            double tempMax = forecast.getAsJsonObject("main").get("temp_max").getAsDouble();
            double tempMin = forecast.getAsJsonObject("main").get("temp_min").getAsDouble();
            int humidity = forecast.getAsJsonObject("main").get("humidity").getAsInt();
            double windSpeed = forecast.getAsJsonObject("wind").get("speed").getAsDouble();
            String description = forecast.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
            String iconCode = forecast.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();

            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 15; " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 15; " +
                    "-fx-min-width: 160px; " +
                    "-fx-alignment: center; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 2);");

            Label dayLabel = new Label(jourSemaine);
            dayLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
            dayLabel.setTextFill(Color.web("#1a237e"));

            Label dateLabel = new Label(formatDate(dateJour));
            dateLabel.setFont(Font.font("System", 12));
            dateLabel.setTextFill(Color.web("#5c6bc0"));

            ImageView icon = new ImageView(new Image("https://openweathermap.org/img/wn/" + iconCode + "@2x.png"));
            icon.setFitHeight(70);
            icon.setFitWidth(70);

            Label tempLabel = new Label(Math.round(temp) + "°C");
            tempLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
            tempLabel.setTextFill(getTemperatureColor(temp));

            HBox tempRangeBox = new HBox(5);
            tempRangeBox.setAlignment(javafx.geometry.Pos.CENTER);

            ImageView upArrow = new ImageView(new Image(getClass().getResource("/images/up-arrow.png").toExternalForm()));
            upArrow.setFitWidth(12);
            upArrow.setFitHeight(12);

            ImageView downArrow = new ImageView(new Image(getClass().getResource("/images/down-arrow.png").toExternalForm()));
            downArrow.setFitWidth(12);
            downArrow.setFitHeight(12);

            Label tempRangeLabel = new Label(Math.round(tempMax) + "° / " + Math.round(tempMin) + "°");
            tempRangeLabel.setFont(Font.font("System", 12));
            tempRangeLabel.setTextFill(Color.web("#5c6bc0"));

            tempRangeBox.getChildren().addAll(upArrow, tempRangeLabel, downArrow);

            Label descLabel = new Label(capitalizeFirstLetter(description));
            descLabel.setFont(Font.font("System", FontWeight.MEDIUM, 13));
            descLabel.setTextFill(Color.web("#3949ab"));
            descLabel.setWrapText(true);
            descLabel.setMaxWidth(140);

            HBox detailsBox = new HBox(15);
            detailsBox.setAlignment(javafx.geometry.Pos.CENTER);

            ImageView humidityIcon = new ImageView(new Image(getClass().getResource("/images/humidity.png").toExternalForm()));
            humidityIcon.setFitWidth(16);
            humidityIcon.setFitHeight(16);

            ImageView windIcon = new ImageView(new Image(getClass().getResource("/images/wind.png").toExternalForm()));
            windIcon.setFitWidth(16);
            windIcon.setFitHeight(16);

            Label humidityLabel = new Label(humidity + "%");
            humidityLabel.setFont(Font.font("System", 11));
            humidityLabel.setTextFill(Color.web("#5c6bc0"));
            humidityLabel.setGraphic(humidityIcon);

            Label windLabel = new Label(windSpeed + " km/h");
            windLabel.setFont(Font.font("System", 11));
            windLabel.setTextFill(Color.web("#5c6bc0"));
            windLabel.setGraphic(windIcon);

            detailsBox.getChildren().addAll(humidityLabel, windLabel);

            card.getChildren().addAll(dayLabel, dateLabel, icon, tempLabel, tempRangeBox, descLabel, detailsBox);
            weatherContainer.getChildren().add(card);
        }
    }

    private String getDayOfWeek(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateStr, formatter);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String[] days = {"Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"};
        return days[dayOfWeek.getValue() % 7];
    }

    private String formatDate(String dateStr) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        LocalDate date = LocalDate.parse(dateStr, inputFormatter);
        return outputFormatter.format(date);
    }

    private Color getTemperatureColor(double temp) {
        if (temp > 35) return Color.web("#d50000");
        if (temp > 30) return Color.web("#ff3d00");
        if (temp > 25) return Color.web("#ff6d00");
        if (temp > 20) return Color.web("#ffab00");
        if (temp > 15) return Color.web("#00c853");
        if (temp > 10) return Color.web("#0091ea");
        if (temp > 5)  return Color.web("#2962ff");
        return Color.web("#304ffe");
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
