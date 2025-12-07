package com.paytech.payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytech.payment.config.PaytechProperties;
import com.paytech.payment.dto.PaymentRequest;
import com.paytech.payment.dto.PaymentResponse;
import com.paytech.payment.exception.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
class PaytechClientSync implements PaytechClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaytechClientSync.class);

    private final PaytechProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    PaytechClientSync(PaytechProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .build();
    }

    // TODO: make async if we need better scalability
    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        try {
            String requestBody = objectMapper.writeValueAsString(request);
            LOGGER.info("Sending payment request to Paytech API");

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(properties.url()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + properties.token())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            LOGGER.info("Received response with status: {}", response.statusCode());

            // TODO: Add retry logic with exponential backoff
            String body = response.body();
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(body, PaymentResponse.class);
            } else {
                LOGGER.warn("API error response: {}", body);
                try {
                    return objectMapper.readValue(body, PaymentResponse.class);
                } catch (Exception e) {
                    LOGGER.error("Failed to parse error response", e);
                    String errorMessage = extractErrorMessage(body, response.statusCode());
                    throw new PaymentException(errorMessage);
                }
            }
        } catch (PaymentException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to call Paytech API", e);
            throw new PaymentException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    private String extractErrorMessage(String body, int statusCode) {
        try {
            var node = objectMapper.readTree(body);
            if (node.has("message")) {
                return node.get("message").asText();
            }
            if (node.has("error")) {
                return node.get("error").asText();
            }
        } catch (Exception ignored) {
        }
        return "Payment failed with status: " + statusCode;
    }
}
