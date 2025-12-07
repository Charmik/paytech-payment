package com.paytech.payment.service;

import com.paytech.payment.client.PaytechClient;
import com.paytech.payment.dto.PaymentForm;
import com.paytech.payment.dto.PaymentRequest;
import com.paytech.payment.dto.PaymentResponse;
import com.paytech.payment.exception.PaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;
    private TestPaytechClient testClient;

    @BeforeEach
    void setUp() {
        testClient = new TestPaytechClient();
        paymentService = new PaymentService(testClient);
    }

    @Test
    void processPaymentSuccessReturnsRedirectUrl() {
        testClient.setResponse(successResponse("123", "https://pay.tech/redirect/123"));

        PaymentForm form = new PaymentForm(new BigDecimal("100.00"));
        String redirectUrl = paymentService.processPayment(form);

        assertEquals("https://pay.tech/redirect/123", redirectUrl);
    }

    @Test
    void processPaymentFailureThrowsPaymentException() {
        testClient.setResponse(errorResponse(400, "Invalid amount"));

        PaymentForm form = new PaymentForm(new BigDecimal("100.00"));

        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.processPayment(form));

        assertEquals("Invalid amount", exception.getMessage());
    }

    @Test
    void processPaymentNoRedirectUrlThrowsPaymentException() {
        testClient.setResponse(successResponse("123", null));

        PaymentForm form = new PaymentForm(new BigDecimal("100.00"));

        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.processPayment(form));

        assertEquals("Payment failed", exception.getMessage());
    }

    private PaymentResponse successResponse(String id, String redirectUrl) {
        PaymentResponse.Result result = new PaymentResponse.Result(id, null, null, null, null, null, null, null, redirectUrl);
        return new PaymentResponse(null, 200, result, null, null, null, null);
    }

    private PaymentResponse errorResponse(int status, String message) {
        return new PaymentResponse(null, status, null, "Error", message, null, null);
    }

    private static class TestPaytechClient implements PaytechClient {

        private PaymentResponse response;

        void setResponse(PaymentResponse response) {
            this.response = response;
        }

        @Override
        public PaymentResponse createPayment(PaymentRequest request) {
            return response;
        }
    }
}
