package controllers;

import entities.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceReservation;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ReservationTableViewController {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, String> clientColumn;
    @FXML private TableColumn<Reservation, String> machineColumn;
    @FXML private TableColumn<Reservation, String> startColumn;
    @FXML private TableColumn<Reservation, String> endColumn;
    @FXML private TableColumn<Reservation, Void> actionColumn;

    private final ServiceReservation serviceReservation = new ServiceReservation();
    private ObservableList<Reservation> reservationList;

    @FXML
    public void initialize() {
        reservationList = FXCollections.observableArrayList(serviceReservation.getReservations());

        clientColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getClient().getEmail()));
        machineColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMachine().getName()));
        startColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDateDebut().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        ));
        endColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDateFin().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        ));

        addDeleteButtonToTable();
        reservationTable.setItems(reservationList);
    }

    private void addDeleteButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("🗑 Supprimer");

            {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 11px;");
                deleteButton.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());

                    // Supprimer de la base de données
                    serviceReservation.deleteReservation(reservation.getId());

                    // Supprimer de l'affichage
                    reservationList.remove(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/admin-dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) reservationTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
