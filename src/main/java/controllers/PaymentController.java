package controllers;


import services.PaymentService;
import com.stripe.model.PaymentIntent;

public class PaymentController {

    public void processPayment() {
        PaymentService paymentService = new PaymentService();

        // Créer un PaymentIntent de 10.00 EUR
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(1000, "eur");

        // Obtenir le client secret à utiliser dans le frontend pour finaliser le paiement
        String clientSecret = paymentIntent.getClientSecret();

        // Afficher ou envoyer le clientSecret au frontend
        System.out.println("Client Secret: " + clientSecret);

    }
}

