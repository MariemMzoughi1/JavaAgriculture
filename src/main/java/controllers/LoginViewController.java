package controllers;

import entities.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;
import services.Session;
import services.UserService;
import utils.PasswordUtils;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class LoginViewController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label registerLabel;

    private final UserService userService = new UserService();
    private com.sun.net.httpserver.HttpServer server; // 🔵 Declare server here to reuse it

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠️ Email and password are required.");
            return;
        }

        String hashedInput = PasswordUtils.hashPassword(password);

        List<User> users = userService.getAllUsers();
        for (User user : users) {
            if (user.getEmail().equals(email) && PasswordUtils.checkPassword(password, user.getPassword()))
            {
                Session.setCurrentUser(user);
                statusLabel.setText("✅ Login successful!");
                redirectUser();
                return;
            }
        }
        statusLabel.setText("❌ Invalid email or password.");
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/register-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Register");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Failed to load register page.");
        }
    }

    @FXML
    private void handleGoogleLogin() {


            // 🔵 Start server first if not already running
            if (server == null) {
                startLocalServer(clientId, clientSecret, redirectUri);
            }

            // 🔵 Then open browser
            String authUrl = "https://accounts.google.com/o/oauth2/v2/auth"
                    + "?client_id=" + clientId
                    + "&redirect_uri=" + redirectUri
                    + "&response_type=code"
                    + "&scope=email%20profile"
                    + "&access_type=offline";

            Desktop.getDesktop().browse(new URI(authUrl));

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Failed to start Google login.");
        }
    }

    private void startLocalServer(String clientId, String clientSecret, String redirectUri) {
        new Thread(() -> {
            try {
                server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8888), 0);
                server.createContext("/callback", exchange -> {
                    String query = exchange.getRequestURI().getQuery();
                    String[] params = query.split("&");
                    String code = null;

                    for (String param : params) {
                        if (param.startsWith("code=")) {
                            code = param.split("=")[1];
                            break;
                        }
                    }

                    // 🔥 Prepare a good response page
                    String response = "<html><body><h1> Login successful! You can return to the application.</h1></body></html>";
                    exchange.getResponseHeaders().add("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    try (var os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                    exchange.close();

                    // 🔵 Stop server
                    if (server != null) {
                        server.stop(1);
                        server = null;
                    }

                    // 🔥 Log if we get the code
                    System.out.println("Received code: " + code);

                    if (code != null) {
                        exchangeCodeForToken(code, clientId, clientSecret, redirectUri);
                    } else {
                        System.out.println("⚠️ No code received from Google.");
                    }
                });
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void exchangeCodeForToken(String code, String clientId, String clientSecret, String redirectUri) {
        try {
            URL url = new URL("https://oauth2.googleapis.com/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String params = "code=" + code
                    + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret
                    + "&redirect_uri=" + redirectUri
                    + "&grant_type=authorization_code";

            conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));

            Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject json = new JSONObject(responseBody);
            String accessToken = json.getString("access_token");

            fetchUserInfoFromGoogle(accessToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchUserInfoFromGoogle(String accessToken) {
        try {
            URL url = new URL("https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject userInfo = new JSONObject(responseBody);

            String email = userInfo.getString("email");
            String name = userInfo.getString("name");

            Platform.runLater(() -> {
                List<User> users = userService.getAllUsers();
                for (User user : users) {
                    if (user.getEmail().equals(email)) {
                        Session.setCurrentUser(user);
                        statusLabel.setText("✅ Google login successful!");
                        redirectUser();
                        return;
                    }
                }

                // ❗ User does not exist → Create new
                User newUser = new User(email, "update_password", "Agriculteur", null);
                userService.addUser(newUser);
                Session.setCurrentUser(newUser);

                statusLabel.setText("✅ Google account created and login successful!");
                redirectUser();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redirectUser() {
        try {
            User user = Session.getCurrentUser();
            String role = user.getRole().toUpperCase();

            String fxmlPath;
            if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
                fxmlPath = "/com/example/projectjava/admin-dashboard-view.fxml";
            } else {
                fxmlPath = "/com/example/projectjava/machine-home.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 1000, 800);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Dashboard");

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Failed to load dashboard.");
        }


    }

    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projectjava/send-reset-token.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Mot de passe oublié");
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Erreur lors du chargement de la page de réinitialisation.");
        }
    }

}
