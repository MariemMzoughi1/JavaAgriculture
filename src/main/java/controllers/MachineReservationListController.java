package controllers;




import entities.Machine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceMachine;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MachineReservationListController {

    @FXML
    private TilePane machineTilePane;

    private final ServiceMachine serviceMachine = new ServiceMachine();

    @FXML
    public void initialize() {
        List<Machine> machines = serviceMachine.getMachines();

        for (Machine machine : machines) {
            VBox card = createMachineCard(machine);
            machineTilePane.getChildren().add(card);
        }
    }

    private VBox createMachineCard(Machine machine) {
        VBox card = new VBox(10);
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.2, 0, 2);");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(120);
        String imagePath = (machine.getImage_url() != null && new File(machine.getImage_url()).exists())
                ? new File(machine.getImage_url()).toURI().toString()
                : new File("images/default.png").toURI().toString();
        imageView.setImage(new Image(imagePath));

        Label nameLabel = new Label(machine.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label typeLabel = new Label("Type: " + machine.getType());
        Label priceLabel = new Label("Prix: " + machine.getPricePerDay() + " DT/jour");

        Button reserveButton = new Button("Réserver");
        reserveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        reserveButton.setOnAction(event -> openReservationPage(machine));

        card.getChildren().addAll(imageView, nameLabel, typeLabel, priceLabel, reserveButton);
        return card;
    }

    private void openReservationPage(Machine machine) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/reservation-view.fxml"));
            Scene scene = new Scene(loader.load());

            ReservationViewController controller = loader.getController();
            controller.setSelectedMachine(machine); // Envoyer la machine sélectionnée

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Réserver Machine");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/machine-home.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("🏡 Accueil des Machines");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
 



