package controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class WebViewController {

    @FXML
    private WebView webView;

    // Méthode pour charger l'URL dans le WebView
    public void loadPaymentPage(String clientSecret) {
        String paymentPageUrl = "https://yourwebsite.com/stripe/checkout?client_secret=" + clientSecret;
        webView.getEngine().load(paymentPageUrl);
    }
}
