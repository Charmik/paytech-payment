package com.paytech.payment.dto;

import java.math.BigDecimal;

// TODO: add other fields for different types of requests
public record PaymentRequest(
        String paymentType,
        BigDecimal amount,
        String currency) {

    public static PaymentRequest deposit(BigDecimal amount) {
        return new PaymentRequest("DEPOSIT", amount, "EUR");
    }
}
