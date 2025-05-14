package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.User;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.Session;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MachineHomeController {

    @FXML private VBox newsList;
    @FXML private AnchorPane mainContentArea;

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

    // ✅ Correction : Parent au lieu d’AnchorPane + transition fade-in
    private void loadPage(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent newContent = loader.load();

        AnchorPane.setTopAnchor(newContent, 0.0);
        AnchorPane.setBottomAnchor(newContent, 0.0);
        AnchorPane.setLeftAnchor(newContent, 0.0);
        AnchorPane.setRightAnchor(newContent, 0.0);

        mainContentArea.getChildren().setAll(newContent);

        // ✅ Transition animée
        FadeTransition ft = new FadeTransition(Duration.millis(400), newContent);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    // ---- Handlers ----

    @FXML private void handleAddMachine(ActionEvent event) throws IOException {
        loadPage("/com/example/projectjava/machine-add.fxml");
    }

    @FXML private void handleListMachines(ActionEvent event) throws IOException {
        loadPage("/com/example/projectjava/machine-index.fxml");
    }

    @FXML private void handleReservation(ActionEvent event) throws IOException {
        loadPage("/com/example/projectjava/reservation-view.fxml");
    }

    @FXML private void handleOpenMachineReservationList(ActionEvent event) throws IOException {
        loadPage("/com/example/projectjava/machine-reservation-list.fxml");
    }

    @FXML private void handleForum(ActionEvent event) throws IOException {
        loadPage("/com/example/projectjava/ListeForum.fxml");
    }

    @FXML private void handleAccueil(ActionEvent event) throws IOException {
        loadPage("/com/example/projectjava/Acceuil.fxml");
    }

    @FXML private void handleProduits(ActionEvent event) throws IOException {
        loadPage("/com/example/projectjava/AfficherProduit.fxml");
    }

    @FXML private void handleCommande(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/commande.fxml"));
            Parent commandeView = loader.load();

            AnchorPane.setTopAnchor(commandeView, 0.0);
            AnchorPane.setBottomAnchor(commandeView, 0.0);
            AnchorPane.setLeftAnchor(commandeView, 0.0);
            AnchorPane.setRightAnchor(commandeView, 0.0);

            mainContentArea.getChildren().setAll(commandeView);

            FadeTransition ft = new FadeTransition(Duration.millis(400), commandeView);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Chargement échoué");
            alert.setContentText("Impossible de charger la vue commande.");
            alert.showAndWait();
        }
    }

    @FXML private void handleCulture(ActionEvent event) throws IOException {
        loadPage("/com/example/projectjava/AfficherCulture.fxml");
    }

    @FXML private void handleParcelle(ActionEvent event) throws IOException {
        loadPage("/com/example/projectjava/AfficherParcelle.fxml");
    }

    @FXML private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/edit-user-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);

            User currentUser = Session.getCurrentUser();
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

    @FXML private void handleLogout(ActionEvent event) {
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
}
