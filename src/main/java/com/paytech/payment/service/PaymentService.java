package com.paytech.payment.service;

import com.paytech.payment.client.PaytechClient;
import com.paytech.payment.dto.PaymentForm;
import com.paytech.payment.dto.PaymentRequest;
import com.paytech.payment.dto.PaymentResponse;
import com.paytech.payment.exception.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    private final PaytechClient paytechClient;

    public PaymentService(PaytechClient paytechClient) {
        this.paytechClient = paytechClient;
    }

    public String processPayment(PaymentForm form) {
        LOGGER.info("Processing payment for amount: {}", form.amount());

        // TODO: Add database for persistence
        PaymentRequest request = PaymentRequest.deposit(form.amount());
        PaymentResponse response = paytechClient.createPayment(request);

        if (response.isSuccess()) {
            LOGGER.info("Payment successful, redirecting to: {}", response.getRedirectUrl());
            return response.getRedirectUrl();
        } else {
            LOGGER.warn("Payment failed: {}", response.getErrorMessage());
            throw new PaymentException(response.getErrorMessage());
        }
    }
}
