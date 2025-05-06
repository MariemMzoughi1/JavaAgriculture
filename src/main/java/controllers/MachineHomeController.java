package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.Session;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MachineHomeController {

    @FXML
    private VBox newsList;

    @FXML
    public void initialize() {
        loadNews();
    }

    private void loadNews() {
        new Thread(() -> {
            try {
                URL url = new URL("https://newsapi.org/v2/everything?q=agriculture&apiKey=fb77fa4ee8d247e7b0f5e989b4684b70");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                JsonObject jsonResponse = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                JsonArray articles = jsonResponse.getAsJsonArray("articles");

                Platform.runLater(() -> {
                    newsList.getChildren().clear();
                    for (int i = 0; i < Math.min(10, articles.size()); i++) {
                        JsonObject article = articles.get(i).getAsJsonObject();
                        String title = article.get("title").getAsString();
                        String link = article.get("url").getAsString();

                        Hyperlink newsLink = new Hyperlink(title);
                        newsLink.setOnAction(e -> getHostServices().showDocument(link));
                        newsLink.setWrapText(true);
                        newsList.getChildren().add(newsLink);
                    }
                });

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private javafx.application.HostServices getHostServices() {
        return (javafx.application.HostServices) newsList.getScene().getWindow().getProperties().get("hostServices");
    }

    @FXML
    private void handleAddMachine(ActionEvent event) throws IOException {
        loadPage(event, "/com/example/projectjava/machine-add.fxml");
    }

    @FXML
    private void handleForum(ActionEvent event) throws IOException {
        loadPage(event, "/com/example/projectjava/ListeForum.fxml");
    }

    @FXML
    private void handleListMachines(ActionEvent event) throws IOException {
        loadPage(event, "/com/example/projectjava/machine-index.fxml");
    }

    @FXML
    private void handleReservation(ActionEvent event) throws IOException {
        loadPage(event, "/com/example/projectjava/reservation-view.fxml");
    }

    @FXML
    private void handleCulture(ActionEvent event) throws IOException {
        loadPage(event, "/com/example/projectjava/AfficherCulture.fxml");
    }

    @FXML
    private void handleParcelle(ActionEvent event) throws IOException {
        loadPage(event, "/com/example/projectjava/AfficherParcelle.fxml");
    }

    private void loadPage(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("🌿 DevHarvest - Gestion");
        stage.show();
    }

    @FXML
    private void handleAccueil(ActionEvent event) throws IOException {
        loadPage(event, "/com/example/projectjava/Acceuil.fxml");
    }

    @FXML
    private void handleProduits(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/AfficherProduit.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleLogout(ActionEvent event){
        Session.clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/edit-user-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);

            // Pass current user
            User currentUser = services.Session.getCurrentUser();
            EditUserController controller = loader.getController();
            controller.setUser(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Modifier Profil");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenMachineReservationList(ActionEvent event) throws IOException {
        loadPage(event, "/com/example/projectjava/machine-reservation-list.fxml");
    }
}