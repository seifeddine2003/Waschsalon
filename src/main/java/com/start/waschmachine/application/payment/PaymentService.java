package com.start.waschmachine.application.payment;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements IPaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public String createPaymentIntent(double amountEuros) throws Exception {
        if (amountEuros < 5) {
            throw new IllegalArgumentException("Minimum amount is €5.00");
        }

        Stripe.apiKey = stripeSecretKey;
        long amountCents = Math.round(amountEuros * 100);

        PaymentIntent intent = PaymentIntent.create(
                PaymentIntentCreateParams.builder()
                        .setAmount(amountCents)
                        .setCurrency("eur")
                        .build()
        );

        return intent.getClientSecret();
    }
}
