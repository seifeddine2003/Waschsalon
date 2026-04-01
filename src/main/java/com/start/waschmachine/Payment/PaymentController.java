package com.start.waschmachine.Payment;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> body) {
        try {
            Stripe.apiKey = stripeSecretKey;

            double amountEuros = ((Number) body.get("amount")).doubleValue();

            if (amountEuros < 5) {
                return ResponseEntity.badRequest().body(Map.of("error", "Minimum amount is €5.00"));
            }

            long amountCents = Math.round(amountEuros * 100);

            PaymentIntent intent = PaymentIntent.create(
                PaymentIntentCreateParams.builder()
                    .setAmount(amountCents)
                    .setCurrency("eur")
                    .build()
            );

            return ResponseEntity.ok(Map.of("clientSecret", intent.getClientSecret()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
