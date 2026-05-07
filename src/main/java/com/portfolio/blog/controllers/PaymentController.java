package com.portfolio.blog.controllers;

import com.portfolio.blog.services.PaymentServiceInterface;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/payment")
public class PaymentController {

    private final PaymentServiceInterface paymentService;
    // Secret that is user to create webhook signature
    private final String webhookSecret;

    public PaymentController(
            PaymentServiceInterface paymentService,
            @Value("${stripe.webhook.secret}") String webhookSecret) {
        this.paymentService = paymentService;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping(path = "/checkout")
    public ResponseEntity<String> createCheckout() {
        return ResponseEntity.ok().body(paymentService.createCheckoutSession());
    }

    @PostMapping(path = "/webhook")
    public ResponseEntity<HttpStatus> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            System.out.println(event.toString());
            System.out.println(" \n \n \n ");

        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }

     return ResponseEntity.status(HttpStatus.OK).build();
    }
}
