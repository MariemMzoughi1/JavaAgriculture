package controllers;

import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserListViewController {

    @FXML
    private ComboBox<String> filterRole;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, Void> actionsColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField userInput;

    private final UserService userService = new UserService();
    private final ObservableList<User> masterUserList = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;

    @FXML
    private void handleAddUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/register-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) usersTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/admin-dashboard-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) usersTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        masterUserList.addAll(userService.getAllUsers());
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
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/edit-user-view.fxml"));
                        AnchorPane root = loader.load();
                        EditUserController controller = loader.getController();
                        controller.setUser(user);

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Utilisateur");
                        stage.setScene(new Scene(root, 400, 300));
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                deleteButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    userService.deleteUser(user);
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

    @FXML
    private void handleSendMessage() {
        String userText = userInput.getText().trim();
        if (!userText.isEmpty()) {
            chatArea.appendText("👤 Admin: " + userText + "\n");
            userInput.clear();

            String botResponse = getAdminBotResponse(userText);
            chatArea.appendText("🤖 Assistant: " + botResponse + "\n");
        }
    }

    private String getAdminBotResponse(String message) {
        message = message.toLowerCase();

        if (message.contains("ajouter") || message.contains("nouvel utilisateur") || message.contains("inscrire")) {
            return "Pour ajouter un nouvel utilisateur, clique sur ➕ 'Add New User'.";
        }
        if (message.contains("modifier") || message.contains("éditer") || message.contains("changer")) {
            return "Pour modifier un utilisateur, clique sur ✏️ 'edit' dans la colonne Actions.";
        }
        if (message.contains("supprimer") || message.contains("effacer") || message.contains("retirer")) {
            return "Pour supprimer un utilisateur, clique sur ❌ 'X' dans la colonne Actions.";
        }
        if (message.contains("rechercher") || message.contains("trouver")) {
            return "Utilise le champ de recherche en haut.";
        }
        if (message.contains("filtrer") || message.contains("rôle")) {
            return "Utilise la liste déroulante pour filtrer par rôle.";
        }
        if (message.contains("rollback") || message.contains("restaurer")) {
            return "Le bouton 'Rollback' restaurera la liste.";
        }
        if (message.contains("exporter") || message.contains("sauvegarder")) {
            return "La fonctionnalité 'Export' sera bientôt disponible.";
        }
        if (message.contains("tableau") || message.contains("affichage") || message.contains("liste")) {
            return "La table liste tous les utilisateurs enregistrés.";
        }
        if (message.contains("aide") || message.contains("besoin") || message.contains("support")) {
            return "Je suis là pour t'aider. Pose-moi une question.";
        }

        return "Désolé, je n'ai pas compris. Essaie d'utiliser des mots comme ajouter, modifier, supprimer, filtrer.";
    }

    @FXML
    private void handleShowStatistics() {
        Stage statStage = new Stage();
        statStage.setTitle("📊 Statistiques des utilisateurs");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Rôle");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre d'utilisateurs");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Distribution par Rôle");

        Map<String, Integer> roleCounts = new HashMap<>();
        for (User user : masterUserList) {
            String role = user.getRole();
            if (role != null) {
                role = role.replace("ROLE_", "").toUpperCase();
                roleCounts.put(role, roleCounts.getOrDefault(role, 0) + 1);
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : roleCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);

        VBox root = new VBox(barChart);
        root.setPadding(new Insets(15));
        Scene scene = new Scene(root, 600, 400);

        statStage.setScene(scene);
        statStage.show();
    }
}
