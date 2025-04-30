package Controllers.produit;

import Entites.Machine;
import Entites.Reservation;
import Entites.User;
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
import Services.ServiceMachine;
import Services.ServiceReservation;
import Services.UserService;

import java.io.File;
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

        User user = userService.getUserById(9);
        if (user == null) {
            messageLabel.setText("❌ Utilisateur non trouvé (ID 2).");
            return;
        }
        User user1 = userService.getUserById(11);
        if (user1 == null) {
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

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/machine-home.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("🌾 Accueil des Machines");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}