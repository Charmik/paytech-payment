package com.paytech.payment.controller;

import com.paytech.payment.client.PaytechClient;
import com.paytech.payment.dto.PaymentForm;
import com.paytech.payment.dto.PaymentRequest;
import com.paytech.payment.dto.PaymentResponse;
import com.paytech.payment.exception.PaymentException;
import com.paytech.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaymentControllerTest {

    private PaymentController controller;
    private TestPaytechClient testClient;

    @BeforeEach
    void setUp() {
        testClient = new TestPaytechClient();
        PaymentService paymentService = new PaymentService(testClient);
        controller = new PaymentController(paymentService);
    }

    @Test
    void showPaymentFormReturnsPaymentPage() {
        Model model = new ExtendedModelMap();
        String viewName = controller.showPaymentForm(model);

        assertEquals("payment", viewName);
        assertTrue(model.containsAttribute("paymentForm"));
    }

    @Test
    void processPaymentValidAmountRedirectsToPaytechUrl() {
        PaymentResponse.Result result = new PaymentResponse.Result("123", null, null, null, null, null, null, null, "https://pay.tech/redirect/123");
        testClient.setResponse(new PaymentResponse(null, 200, result, null, null, null, null));

        PaymentForm form = new PaymentForm(new BigDecimal("100.00"));
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "paymentForm");
        Model model = new ExtendedModelMap();

        String viewName = controller.processPayment(form, bindingResult, model);

        assertEquals("redirect:https://pay.tech/redirect/123", viewName);
    }

    @Test
    void processPaymentBindingErrorsReturnsFormPage() {
        PaymentForm form = new PaymentForm(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "paymentForm");
        bindingResult.rejectValue("amount", "NotNull", "Amount is required");
        Model model = new ExtendedModelMap();

        String viewName = controller.processPayment(form, bindingResult, model);

        assertEquals("payment", viewName);
    }

    @Test
    void processPaymentApiErrorThrowsPaymentException() {
        testClient.setResponse(new PaymentResponse(null, 400, null, "Bad Request", "Invalid amount", null, null));

        PaymentForm form = new PaymentForm(new BigDecimal("100.00"));
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "paymentForm");
        Model model = new ExtendedModelMap();

        assertThrows(PaymentException.class,
                () -> controller.processPayment(form, bindingResult, model));
    }

    @Test
    void showErrorPageReturnsErrorPage() {
        String viewName = controller.showErrorPage();
        assertEquals("error", viewName);
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
