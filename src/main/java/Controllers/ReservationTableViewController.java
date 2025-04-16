package Controllers;

import Entites.Reservation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import Services.ServiceReservation;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ReservationTableViewController {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, String> clientColumn;
    @FXML private TableColumn<Reservation, String> machineColumn;
    @FXML private TableColumn<Reservation, String> startColumn;
    @FXML private TableColumn<Reservation, String> endColumn;

    private final ServiceReservation serviceReservation = new ServiceReservation();

    @FXML
    public void initialize() {
        var reservationList = FXCollections.observableArrayList(serviceReservation.getReservations());

        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        clientColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getClient().getEmail()));
        machineColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMachine().getName()));
        startColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDateDebut().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
        );
        endColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDateFin().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
        );

        reservationTable.setItems(reservationList);
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(),800,600);
            Stage stage = (Stage) reservationTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
