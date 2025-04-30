package Controllers.user;

import Entites.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import Services.UserService;

import java.io.IOException;

public class UserListViewController {
    @FXML
    private ComboBox<String> filterRole;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Void> actionsColumn;

    @FXML
    private TextField searchField;

    private final UserService userService = new UserService();
    private final ObservableList<User> masterUserList = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register-view.fxml"));
            Scene scene = new Scene(loader.load(),800,600);
            Stage stage = (Stage) usersTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRollback() {
        masterUserList.clear();
        masterUserList.addAll(userService.afficherUsers());
        searchField.clear();
        filterRole.setValue("All");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rollback");
        alert.setHeaderText(null);
        alert.setContentText("✅ La liste des utilisateurs a été restaurée.");
        alert.getDialogPane().setStyle(
                "-fx-font-size: 14px; -fx-font-family: 'Segoe UI'; -fx-background-color: #f9f9f9; -fx-border-color: #cccccc;"
        );
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin-dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(),800,600);
            Stage stage = (Stage) usersTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        masterUserList.addAll(userService.afficherUsers());
        filteredData = new FilteredList<>(masterUserList, p -> true);
        usersTable.setItems(filteredData);

        filterRole.getItems().addAll("All", "Admin", "Agriculteur", "Fournisseur", "Client");
        filterRole.setValue("All");

        Runnable updateFilter = () -> {
            String emailText = searchField.getText().toLowerCase().trim();
            String selectedRole = filterRole.getValue();

            filteredData.setPredicate(user -> {
                boolean matchesEmail = emailText.isEmpty()
                        || user.getEmail().toLowerCase().contains(emailText);

                boolean matchesRole = true;
                if (!"All".equalsIgnoreCase(selectedRole)) {
                    String roleKey = selectedRole.toUpperCase();
                    matchesRole = user.getRole() != null &&
                            (user.getRole().equalsIgnoreCase(roleKey) ||
                                    user.getRole().equalsIgnoreCase("ROLE_" + roleKey));
                }

                return matchesEmail && matchesRole;
            });
        };

        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter.run());
        filterRole.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter.run());

        addEditDeleteButtons();
    }

    private void addEditDeleteButtons() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("edit");
            private final Button deleteButton = new Button("X");

            {
                editButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
                editButton.setPrefWidth(40);
                deleteButton.setPrefWidth(40);

                editButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    System.out.println("Edit clicked for: " + user.getEmail());
                    // TODO: ouvrir l'interface de modification
                });

                deleteButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    userService.supprimerUser(user.getId());
                    masterUserList.remove(user);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("User Deleted");
                    alert.setHeaderText(null);
                    alert.setContentText("✅ User \"" + user.getEmail() + "\" supprimé avec succès.");
                    alert.getDialogPane().setStyle(
                            "-fx-font-size: 14px; -fx-font-family: 'Segoe UI'; -fx-background-color: #f9f9f9; -fx-border-color: #cccccc;"
                    );
                    alert.showAndWait();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, editButton, deleteButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }
}
