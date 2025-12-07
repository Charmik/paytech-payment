package com.paytech.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentResponse(
        String timestamp,
        int status,
        Result result,
        String error,
        String message,
        List<ValidationError> errors,
        String path) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            String id,
            String referenceId,
            String created,
            String paymentType,
            PaymentState state,
            String description,
            String parentPaymentId,
            PaymentMethod paymentMethod,
            String redirectUrl) {
    }

    public enum PaymentState {
        CHECKOUT, PENDING, AUTHORIZED, CANCELLED, DECLINED, COMPLETED
    }

    public enum PaymentMethod {
        AHLPAY, ALFAKIT; // TODO: add others
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ValidationError(
            List<String> codes,
            List<Argument> arguments,
            String defaultMessage,
            String objectName,
            String field,
            boolean bindingFailure) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Argument(
            List<String> codes,
            String defaultMessage) {
    }

    public boolean isSuccess() {
        return result != null && result.redirectUrl() != null;
    }

    public String getRedirectUrl() {
        return result != null ? result.redirectUrl() : null;
    }

    public String getErrorMessage() {
        if (message != null) {
            return message;
        }
        if (errors != null && !errors.isEmpty()) {
            ValidationError firstError = errors.getFirst();
            if (firstError.defaultMessage() != null) {
                return firstError.defaultMessage();
            }
            if (firstError.field() != null) {
                return "Invalid field: " + firstError.field();
            }
        }
        if (error != null) {
            return error;
        }
        return "Payment failed";
    }
}
