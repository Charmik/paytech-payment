package com.paytech.payment;

import com.paytech.payment.client.PaytechClient;
import com.paytech.payment.dto.PaymentRequest;
import com.paytech.payment.dto.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentApplicationTests {

    @Autowired
    private PaytechClient paytechClient;

    @Test
    void createPaymentSuccess() {
        PaymentRequest request = PaymentRequest.deposit(new BigDecimal("10.00"));
        PaymentResponse response = paytechClient.createPayment(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getRedirectUrl());
    }

    @Test
    void createPaymentFailure() {
        PaymentRequest request = new PaymentRequest("INVALID_TYPE", new BigDecimal("10.00"), "EUR");
        PaymentResponse response = paytechClient.createPayment(request);

        assertFalse(response.isSuccess());
        assertNotNull(response.getErrorMessage());
    }
}
