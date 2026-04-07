package com.start.waschmachine.application.payment;

public interface IPaymentService {
    String createPaymentIntent(double amountEuros) throws Exception;
}
