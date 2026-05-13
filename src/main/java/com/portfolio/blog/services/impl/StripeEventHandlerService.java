package com.portfolio.blog.services.impl;

import com.portfolio.blog.services.StripeEventHandlerServiceInterface;
import com.stripe.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StripeEventHandlerService implements StripeEventHandlerServiceInterface {

    /*

        Method handle() accepts an event and passes to handler method based on event type;

        Need to handle:

        customer.subscription.created    → create Subscription row
        customer.subscription.updated    → update Subscription row (status, period, cancelAtPeriodEnd)
        customer.subscription.deleted    → mark Subscription as canceled
        invoice.paid                     → append to PaymentHistory
        invoice.payment_failed           → append to PaymentHistory, optionally notify user

     */
    @Override
    public void handle(Event event) {

        switch (event.getType()) {

            case "customer.subscription.created" -> handleCustomerSubscriptionCreated(event);

            case "customer.subscription.updated" -> handleCustomerSubscriptionUpdated(event);

            case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted(event);

            case "invoice.paid" -> handleInvoicePaid(event);

            case "invoice.payment_failed" -> handleInvoicePaymentFailed(event);

            default -> log.info("Stripe webhook received of type: {}", event.getType());
        }
    }

    @Override
    public void handleCustomerSubscriptionCreated(Event event) {
        log.info("Stripe webhook received of type: {}", event.getType());
        log.info(event.toString());
        //implementation
    }

    @Override
    public void handleCustomerSubscriptionUpdated(Event event) {
        log.info("Stripe webhook received of type: {}", event.getType());
        log.info(event.toString());
        //implementation
    }

    @Override
    public void handleCustomerSubscriptionDeleted(Event event) {
        log.info("Stripe webhook received of type: {}", event.getType());
        //implementation
    }

    @Override
    public void handleInvoicePaid(Event event) {
        log.info("Stripe webhook received of type: {}", event.getType());
        //implementation
    }

    @Override
    public void handleInvoicePaymentFailed(Event event) {
        log.info("Stripe webhook received of type: {}", event.getType());
        //implementation
    }

}