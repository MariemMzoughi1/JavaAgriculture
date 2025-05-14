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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
    @FXML private Label weatherLabel;

    private final ServiceMachine serviceMachine = new ServiceMachine();
    private final ServiceReservation serviceReservation = new ServiceReservation();
    private final UserService userService = new UserService();

    // 🔁 Adapter ce chemin vers ton dossier Symfony local
    private static final String SYMFONY_MACHINE_DIR = "C:/Users/DAMIANO/pidevvvvvvvvv/DevHarvest-forum/public/uploads/images/";

    private final String API_KEY = "783ac35576b137642b4f2f2e061714e1"; // Clé API OpenWeather

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

        String imagePath;
        if (machine.getImage_url() != null) {
            File imageFile = new File(SYMFONY_MACHINE_DIR + machine.getImage_url());
            if (imageFile.exists()) {
                imagePath = imageFile.toURI().toString();
            } else {
                imagePath = new File("images/default.png").toURI().toString();
            }
        } else {
            imagePath = new File("images/default.png").toURI().toString();
        }

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

        endDatePicker.setDayCellFactory(startDatePicker.getDayCellFactory());
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
            messageLabel.setText("❌ Utilisateur non trouvé.");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchWeather() {
        String city = "Tunis";
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
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

            weatherLabel.setText("🌤 Température : " + temperature + "°C, " + description);
        } catch (IOException e) {
            weatherLabel.setText("❌ Impossible de récupérer la météo");
        }
    }

    // === Chatbot ===
    @FXML
    private void handleSend() {
        String input = userInput.getText();
        if (input.isEmpty()) return;

        chatArea.appendText("[Vous] : " + input + "\n");
        String response = getBotResponse(input);
        chatArea.appendText("[Bot] : " + response + "\n");
        userInput.clear();
    }

    private String getBotResponse(String input) {
        input = input.toLowerCase();

        if (input.contains("bonjour") || input.contains("salut")) return "👋 Bonjour ! Comment puis-je vous aider ?";
        if (input.contains("types de machines")) return "🚜 Tracteurs, moissonneuses, semoirs, herses, pulvérisateurs...";
        if (input.contains("prix") && input.contains("2025")) return "💸 Entre 250 et 400 DT/jour selon le modèle.";
        if (input.contains("réserver")) return "📅 Oui, vous pouvez réserver pour plusieurs jours.";
        if (input.contains("entretenir")) return "🛠️ Nettoyer, graisser, vérifier les fluides et les filtres.";
        if (input.contains("météo")) return "🌦️ Consultez la météo en haut du formulaire.";
        if (input.contains("merci")) return "🙏 Avec plaisir !";

        return "🤔 Je ne connais pas encore cette information. Essayez une autre question.";
    }

    @FXML private void handleQuickTypes() {
        userInput.setText("Quels types de machines agricoles existent ?");
        handleSend();
    }

    @FXML private void handleQuickPrice() {
        userInput.setText("Quel est le prix de location d'un tracteur en 2025 ?");
        handleSend();
    }

    @FXML private void handleQuickMaintenance() {
        userInput.setText("Comment entretenir une moissonneuse-batteuse ?");
        handleSend();
    }

    @FXML private void handleQuickReservation() {
        userInput.setText("Puis-je réserver une machine pour une semaine ?");
        handleSend();
    }
}
