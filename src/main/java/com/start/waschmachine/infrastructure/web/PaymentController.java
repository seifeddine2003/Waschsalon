package com.start.waschmachine.infrastructure.web;

import com.start.waschmachine.application.payment.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> body) {
        try {
            double amountEuros = ((Number) body.get("amount")).doubleValue();
            String clientSecret = paymentService.createPaymentIntent(amountEuros);
            return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
