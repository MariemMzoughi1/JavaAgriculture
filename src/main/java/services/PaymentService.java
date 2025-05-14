package services;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

public class PaymentService {

    public PaymentService() {
        // Initialise Stripe avec ta clé secrète
        Stripe.apiKey = "sk_test_51QxrcZB2VudiYA1MyVulxNng9wn0TvukmqGXavh081A0vVoMjdD7ftt6TpoyuIstUUU8zzAmbf9U73dHjqLhxnda00RNtbqKAT"; // ← ta clé secrète
    }

    public Session createCheckoutSession(double amount) throws Exception {
        long amountInCents = (long) (amount * 100); // Stripe attend des centimes

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://yourwebsite.com/success") // Lien après paiement réussi
                .setCancelUrl("https://yourwebsite.com/cancel")   // Lien si l'utilisateur annule
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")  // Ou "usd" selon ton besoin
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Commande Fruits et Légumes")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        return Session.create(params);
    }

    public PaymentIntent createPaymentIntent(int i, String eur) {
        return null;
    }
}
