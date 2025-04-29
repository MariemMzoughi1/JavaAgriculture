package Controllers.machine;

import Entites.Machine;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Services.ServiceMachine;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MachineIndexController {

    @FXML
    private FlowPane machineContainer;

    private final ServiceMachine serviceMachine = new ServiceMachine();

    @FXML
    public void initialize() {
        loadMachines();
    }

    private void loadMachines() {
        machineContainer.getChildren().clear();
        List<Machine> machines = serviceMachine.getMachines();

        for (Machine machine : machines) {
            VBox card = new VBox(10);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPrefWidth(200);
            card.setMinWidth(200);
            card.setMaxWidth(200);
            card.setPrefHeight(320);
            card.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10;"
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0.1, 0, 2);");

            // 🖼️ Load image or fallback
            String imagePath;
            File userImage = (machine.getImage_url() != null) ? new File(machine.getImage_url()) : null;

            if (userImage != null && userImage.exists()) {
                imagePath = userImage.toURI().toString();
            } else {
                File fallback = new File("images/default.png");
                if (fallback.exists()) {
                    imagePath = fallback.toURI().toString();
                } else {
                    System.err.println("⚠️ Fallback image not found at images/default.png");
                    continue;
                }
            }

            ImageView imageView = new ImageView(new Image(imagePath, true));
            imageView.setFitHeight(130);
            imageView.setFitWidth(180);
            imageView.setPreserveRatio(false);

            // 📄 Labels
            Label nameLabel = new Label(machine.getName());
            nameLabel.setWrapText(true);
            nameLabel.setMaxWidth(180);
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

            Label typeLabel = new Label(machine.getType());
            typeLabel.setWrapText(true);
            typeLabel.setMaxWidth(180);
            typeLabel.setAlignment(Pos.CENTER);
            typeLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            Label priceLabel = new Label(machine.getPricePerDay() + " DT/jour");
            priceLabel.setStyle("-fx-text-fill: #27ae60;");

            // 🧩 Buttons
            Button editBtn = new Button("✏️ Modifier");
            Button deleteBtn = new Button("🗑️ Supprimer");

            editBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 6;");
            deleteBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 6;");
            editBtn.setPrefWidth(160);
            deleteBtn.setPrefWidth(160);

            editBtn.setOnAction(e -> handleEdit(machine));
            deleteBtn.setOnAction(e -> handleDelete(machine));

            VBox.setMargin(editBtn, new Insets(5, 0, 0, 0));
            VBox.setMargin(deleteBtn, new Insets(0, 0, 10, 0));

            card.getChildren().addAll(imageView, nameLabel, typeLabel, priceLabel, editBtn, deleteBtn);
            machineContainer.getChildren().add(card);
        }
    }

    private void handleEdit(Machine machine) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/machine-edit.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            // Inject machine data
            MachineEditController controller = loader.getController();
            controller.setMachine(machine);

            // Change current scene (not a new window)
            Stage currentStage = (Stage) machineContainer.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.setTitle("✏️ Modifier la Machine");
            currentStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Machine machine) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer la machine : " + machine.getName() + " ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                serviceMachine.deleteMachine(machine); // we assume it handles all errors internally
                showAlert("Suppression terminée.\n(Elle peut échouer si la machine est liée à des réservations.)");
                loadMachines(); // refresh the view
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ Nouveau : handleBack()
    @FXML
    private void handleBack(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/machine-home.fxml"));
            Scene scene = new Scene(loader.load(),800,600);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("🌾 Gestion des Machines");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
