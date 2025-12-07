package com.paytech.payment.client;

import com.paytech.payment.dto.PaymentRequest;
import com.paytech.payment.dto.PaymentResponse;

public interface PaytechClient {

    PaymentResponse createPayment(PaymentRequest request);
}
