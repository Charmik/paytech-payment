package com.paytech.payment.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PaymentException.class)
    public String handlePaymentException(PaymentException ex, Model model) {
        LOGGER.error("Payment error: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        LOGGER.error("Unexpected error", ex);
        model.addAttribute("error", "An unexpected error occurred. Please try again.");
        return "error";
    }
}
