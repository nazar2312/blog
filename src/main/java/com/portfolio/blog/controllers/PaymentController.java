package com.portfolio.blog.controllers;

import com.portfolio.blog.services.StripeCustomerServiceInterface;
import com.portfolio.blog.services.StripeEventHandlerServiceInterface;
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

    private final StripeCustomerServiceInterface customerService;
    private final String webhookSecret; // Secret that is used to create webhook signature
    private final StripeEventHandlerServiceInterface stripeEventHandlerService;

    public PaymentController(
            StripeCustomerServiceInterface customerService,
            @Value("${stripe.webhook.secret}") String webhookSecret, StripeEventHandlerServiceInterface stripeService)
    {
        this.customerService = customerService;
        this.webhookSecret = webhookSecret;
        this.stripeEventHandlerService = stripeService;
    }

    @PostMapping(path = "/checkout")
    public ResponseEntity<String> createCheckout() {
        return ResponseEntity.ok().body(customerService.createCheckoutSession());
    }

    @PostMapping(path = "/manage")
    public ResponseEntity<String> createBillingPortal() {
        return ResponseEntity.ok().body(customerService.createBillingPortalSession());
    }

    @PostMapping(path = "/webhook")
    public ResponseEntity<HttpStatus> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            stripeEventHandlerService.handle(event);

        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
     return ResponseEntity.status(HttpStatus.OK).build();
    }
}
