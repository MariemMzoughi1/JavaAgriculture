package Controllers.machine;

import Entites.Machine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import Services.ServiceMachine;

import java.io.IOException;

public class MachineTableViewController {

    @FXML private TableView<Machine> machineTable;
    @FXML private TableColumn<Machine, Integer> idColumn;
    @FXML private TableColumn<Machine, String> nameColumn;
    @FXML private TableColumn<Machine, String> typeColumn;
    @FXML private TableColumn<Machine, String> etatColumn;
    @FXML private TableColumn<Machine, String> dateColumn;
    @FXML private TableColumn<Machine, Double> priceColumn;
    @FXML private TableColumn<Machine, String> marqueColumn;
    @FXML private TableColumn<Machine, Void> actionsColumn;

    private final ServiceMachine serviceMachine = new ServiceMachine();

    @FXML
    public void initialize() {
        ObservableList<Machine> machineList = FXCollections.observableArrayList(serviceMachine.getMachines());

        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        typeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getType()));
        etatColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEtat()));

        // Format date entretien


        priceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPricePerDay()).asObject());
        marqueColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getBrand()));

        machineTable.setItems(machineList);

        // Ajouter la colonne avec les boutons actions
        addActionButtons();
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");

            {
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                editButton.setPrefWidth(40);
                deleteButton.setPrefWidth(40);

                editButton.setOnAction(event -> {
                    Machine machine = getTableView().getItems().get(getIndex());
                    System.out.println("✏️ Modifier: " + machine.getName());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/machine-edit.fxml"));
                        Scene scene = new Scene(loader.load(), 800, 600);

                        // Inject machine data
                        MachineEditController controller = loader.getController();
                        controller.setMachine(machine);

                        // Change current scene (not a new window)
                        Stage currentStage = (Stage) machineTable.getScene().getWindow();
                        currentStage.setScene(scene);
                        currentStage.setTitle("✏️ Modifier la Machine");
                        currentStage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                deleteButton.setOnAction(event -> {
                    Machine machine = getTableView().getItems().get(getIndex());

                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Confirmation");
                    confirmation.setHeaderText(null);
                    confirmation.setContentText("Voulez-vous vraiment supprimer la machine : " + machine.getName() + " ?");

                    confirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            serviceMachine.deleteMachine(machine);
                            machineTable.getItems().remove(machine);

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Suppression");
                            alert.setHeaderText(null);
                            alert.setContentText("✅ Machine supprimée avec succès.");
                            alert.showAndWait();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(8, editButton, deleteButton);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(),800,600);
            Stage stage = (Stage) machineTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/machine-add.fxml"));
            Scene scene = new Scene(loader.load(),800,600);
            Stage stage = (Stage) machineTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
