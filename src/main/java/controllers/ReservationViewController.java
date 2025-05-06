package controllers;

import entities.Machine;
import entities.Reservation;
import entities.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;
import services.ServiceMachine;
import services.ServiceReservation;
import services.UserService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReservationViewController {

    @FXML private ComboBox<Machine> machineComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Spinner<Integer> startHourSpinner;
    @FXML private Spinner<Integer> startMinuteSpinner;
    @FXML private Spinner<Integer> endHourSpinner;
    @FXML private Spinner<Integer> endMinuteSpinner;
    @FXML private Label messageLabel;

    @FXML private VBox machineCard;
    @FXML private ImageView machineImage;
    @FXML private Label machineName;
    @FXML private Label machineType;
    @FXML private Label machineEtat;
    @FXML private Label machinePrix;
    @FXML private TextArea chatArea;
    @FXML private TextField userInput;

    private final ServiceMachine serviceMachine = new ServiceMachine();
    private final ServiceReservation serviceReservation = new ServiceReservation();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        List<Machine> machines = serviceMachine.getMachines();
        machineComboBox.setItems(FXCollections.observableArrayList(machines));

        machineComboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Machine machine, boolean empty) {
                super.updateItem(machine, empty);
                setText(empty || machine == null ? null : machine.getName());
            }
        });
        machineComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Machine machine, boolean empty) {
                super.updateItem(machine, empty);
                setText(empty || machine == null ? null : machine.getName());
            }
        });

        machineComboBox.setOnAction(e -> {
            Machine selected = machineComboBox.getValue();
            showMachineDetails(selected);
            if (selected != null) {
                disableReservedDates(selected.getId());
            }
        });

        startHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8));
        startMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        endHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 17));
        endMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        machineCard.setVisible(false);
        fetchWeather();
    }

    private void showMachineDetails(Machine machine) {
        if (machine == null) {
            machineCard.setVisible(false);
            return;
        }

        machineName.setText(machine.getName());
        machineType.setText("Type: " + machine.getType());
        machineEtat.setText("État: " + machine.getEtat());
        machinePrix.setText("Prix: " + machine.getPricePerDay() + " DT / jour");

        String imagePath = (machine.getImage_url() != null && new File(machine.getImage_url()).exists())
                ? new File(machine.getImage_url()).toURI().toString()
                : new File("images/default.png").toURI().toString();

        machineImage.setImage(new Image(imagePath));
        machineCard.setVisible(true);
    }

    private void disableReservedDates(int machineId) {
        List<Reservation> reservations = serviceReservation.getReservationsByMachine(machineId);

        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(false);
                for (Reservation res : reservations) {
                    LocalDate resStart = res.getDateDebut().toLocalDateTime().toLocalDate();
                    LocalDate resEnd = res.getDateFin().toLocalDateTime().toLocalDate();
                    if ((date.isEqual(resStart) || date.isEqual(resEnd)) || (date.isAfter(resStart) && date.isBefore(resEnd))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #f8d7da;");
                    }
                }
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(false);
                for (Reservation res : reservations) {
                    LocalDate resStart = res.getDateDebut().toLocalDateTime().toLocalDate();
                    LocalDate resEnd = res.getDateFin().toLocalDateTime().toLocalDate();
                    if ((date.isEqual(resStart) || date.isEqual(resEnd)) || (date.isAfter(resStart) && date.isBefore(resEnd))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #f8d7da;");
                    }
                }
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });
    }

    @FXML
    private void handleReserve() {
        Machine selectedMachine = machineComboBox.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        messageLabel.setText("");
        messageLabel.setStyle("-fx-text-fill: #c0392b;");

        if (selectedMachine == null || start == null || end == null) {
            messageLabel.setText("❌ Veuillez remplir tous les champs.");
            return;
        }

        if (end.isBefore(start)) {
            messageLabel.setText("❌ La date de fin doit être après la date de début.");
            return;
        }

        User user = userService.getUser(34);
        if (user == null) {
            messageLabel.setText("❌ Utilisateur non trouvé (ID 2).");
            return;
        }

        LocalDateTime startDateTime = LocalDateTime.of(start, LocalTime.of(startHourSpinner.getValue(), startMinuteSpinner.getValue()));
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.of(endHourSpinner.getValue(), endMinuteSpinner.getValue()));

        Timestamp startTimestamp = Timestamp.valueOf(startDateTime);
        Timestamp endTimestamp = Timestamp.valueOf(endDateTime);

        boolean available = serviceReservation.isReservationAvailable(selectedMachine.getId(), startTimestamp, endTimestamp);

        if (!available) {
            messageLabel.setText("❌ Cette machine est déjà réservée pendant cette période.");
            return;
        }

        Reservation reservation = new Reservation(startTimestamp, endTimestamp, selectedMachine, user);
        serviceReservation.addReservation(reservation);

        messageLabel.setStyle("-fx-text-fill: #27ae60;");
        messageLabel.setText("✅ Réservation enregistrée avec succès !");
        clearForm();
    }

    private void clearForm() {
        machineComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        machineCard.setVisible(false);
    }
    public void setSelectedMachine(Machine machine) {
        machineComboBox.getSelectionModel().select(machine);
        showMachineDetails(machine);
    }


    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/machine-home.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("🌾 Accueil des Machines");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Label weatherLabel;

    private final String API_KEY = "783ac35576b137642b4f2f2e061714e1"; // Remplace par ta vraie clé OpenWeatherMap

    public void fetchWeather() {
        String city = "Tunis"; // ou tu peux rendre dynamique selon l'utilisateur plus tard
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONObject obj = new JSONObject(content.toString());
            JSONObject main = obj.getJSONObject("main");
            double temperature = main.getDouble("temp");

            JSONObject weather = obj.getJSONArray("weather").getJSONObject(0);
            String description = weather.getString("description");

            // Afficher dans le label
            weatherLabel.setText("🌤 Température : " + temperature + "°C, " + description);

        } catch (IOException e) {
            weatherLabel.setText("❌ Impossible de récupérer la météo");
            System.out.println("Erreur API météo : " + e.getMessage());
        }
    }
    // 💬 Chatbot

    @FXML
    private void handleSend() {
        String input = userInput.getText();
        if (input.isEmpty()) {
            return;
        }

        chatArea.appendText("[Vous] : " + input + "\n");
        String response = getBotResponse(input);
        chatArea.appendText("[Bot] : " + response + "\n");

        userInput.clear();
    }

    private String getBotResponse(String input) {
        input = input.toLowerCase();

        if (input.contains("bonjour") || input.contains("salut")) {
            return "👋 Bonjour ! Comment puis-je vous aider pour vos machines agricoles ?";
        }
        if (input.contains("types de machines") || input.contains("type de machine")) {
            return "🚜 Il existe : tracteurs, moissonneuses-batteuses, semoirs, pulvérisateurs, herses...";
        }
        if (input.contains("prix") && input.contains("location") && input.contains("2025")) {
            return "💸 En 2025, la location d'un tracteur est estimée entre 250 et 400 DT par jour selon le modèle.";
        }
        if (input.contains("réserver") && (input.contains("semaine") || input.contains("jours"))) {
            return "📅 Oui ! Vous pouvez réserver sur plusieurs jours via notre formulaire.";
        }
        if (input.contains("entretenir") && (input.contains("moissonneuse") || input.contains("machine"))) {
            return "🛠️ Entretien : Vérifiez les lames, graissez les roulements, changez l'huile.";
        }
        if (input.contains("météo")) {
            return "🌦️ Consultez la météo agricole au-dessus du formulaire.";
        }
        if (input.contains("merci")) {
            return "🙏 Merci à vous !";
        }

        return "🤔 Désolé, je ne connais pas encore cette information. Essayez une autre question sur les machines agricoles.";
    }

    // 💬 Boutons rapides
    @FXML
    private void handleQuickTypes() {
        userInput.setText("Quels types de machines agricoles existent ?");
        handleSend();
    }

    @FXML
    private void handleQuickPrice() {
        userInput.setText("Quel est le prix de location d'un tracteur en 2025 ?");
        handleSend();
    }

    @FXML
    private void handleQuickMaintenance() {
        userInput.setText("Comment entretenir une moissonneuse-batteuse ?");
        handleSend();
    }

    @FXML
    private void handleQuickReservation() {
        userInput.setText("Puis-je réserver une machine pour une semaine ?");
        handleSend();

}
}