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
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> body) throws Exception {
        double amountEuros = ((Number) body.get("amount")).doubleValue();
        String clientSecret = paymentService.createPaymentIntent(amountEuros);
        return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
    }
}
