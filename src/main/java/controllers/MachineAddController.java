package controllers;

import entities.Machine;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceMachine;
import services.Session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.Objects;

public class MachineAddController {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> etatComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> brandComboBox;
    @FXML private Label imageLabel;
    @FXML private Button uploadButton;
    @FXML private ImageView imagePreview;

    private String relativeImagePath;
    private final ServiceMachine serviceMachine = new ServiceMachine();

    // ✅ Dossier cible Symfony
    private static final String SYMFONY_UPLOAD_DIR = "C:/Users/DAMIANO/pidevvvvvvvvv/DevHarvest-forum/public/uploads/images/";

    @FXML
    public void initialize() {
        etatComboBox.setItems(FXCollections.observableArrayList(
                "Disponible", "En maintenance", "Louée"
        ));

        typeComboBox.setItems(FXCollections.observableArrayList(
                "Tracteur", "Moissonneuse", "Semoir"
        ));

        brandComboBox.setItems(FXCollections.observableArrayList(
                "Massey Ferguson", "John Deere", "Claas"
        ));
    }

    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                File destDir = new File(SYMFONY_UPLOAD_DIR);
                if (!destDir.exists()) destDir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(destDir, fileName);

                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                relativeImagePath = fileName; // ✅ seul le nom du fichier
                imageLabel.setText(fileName);
                imagePreview.setImage(new Image(destFile.toURI().toString()));

            } catch (IOException e) {
                e.printStackTrace();
                imageLabel.setText("Erreur lors de l'importation");
            }
        }
    }

    @FXML
    private void handleAddMachine() {
        if (nameField.getText().isEmpty() ||
                typeComboBox.getValue() == null ||
                etatComboBox.getValue() == null ||
                datePicker.getValue() == null ||
                priceField.getText().isEmpty() ||
                brandComboBox.getValue() == null) {

            showAlert("❌ Veuillez remplir tous les champs obligatoires.");
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceField.getText());
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert("❌ Le prix doit être un nombre entier positif.");
            return;
        }

        Machine machine = new Machine(
                nameField.getText(),
                typeComboBox.getValue(),
                etatComboBox.getValue(),
                Timestamp.valueOf(datePicker.getValue().atStartOfDay()),
                price,
                brandComboBox.getValue(),
                relativeImagePath // ✅ nom de l’image
        );

        serviceMachine.addMachine(machine);
        showAlert("✅ Machine ajoutée avec succès !");
        clearForm();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        nameField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        etatComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        priceField.clear();
        brandComboBox.getSelectionModel().clearSelection();
        imageLabel.setText("Aucune image sélectionnée");
        imagePreview.setImage(null);
        relativeImagePath = null;
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        String fxml;
        if (Objects.equals(Session.getCurrentUser().getRole(), "Admin")) {
            fxml = "/com/example/projectjava/admin-dashboard-view.fxml";
        } else {
            fxml = "/com/example/projectjava/machine-home.fxml";
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Scene scene = new Scene(loader.load(), 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("🌾 Gestion des Machines");
        stage.show();
    }
}
