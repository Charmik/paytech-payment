package com.paytech.payment.controller;

import com.paytech.payment.dto.PaymentForm;
import com.paytech.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/")
    public String showPaymentForm(Model model) {
        model.addAttribute("paymentForm", new PaymentForm(null));
        return "payment";
    }

    @PostMapping("/pay")
    public String processPayment(
            @Valid @ModelAttribute("paymentForm") PaymentForm form,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "payment";
        }

        String redirectUrl = paymentService.processPayment(form);
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/error")
    public String showErrorPage() {
        return "error";
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }
}
