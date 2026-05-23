package com.portfolio.blog.services.impl;

import com.portfolio.blog.services.PaymentHistoryServiceInterface;
import com.portfolio.blog.services.StripeEventHandlerServiceInterface;
import com.portfolio.blog.services.SubscriptionServiceInterface;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeEventHandlerService implements StripeEventHandlerServiceInterface {

    /*
        Method handle() accepts an event and passes to handler method based on event type;
        customer.subscription.created    → create Subscription row
        customer.subscription.updated    → update Subscription row (status, period, cancelAtPeriodEnd)
        customer.subscription.deleted    → mark Subscription as canceled
        invoice.paid                     → append to PaymentHistory
        invoice.payment_failed           → append to PaymentHistory

     */

    private final SubscriptionServiceInterface subscriptionService;
    private final PaymentHistoryServiceInterface paymentHistoryService;

    @Override
    public void handle(Event event) {

        StripeObject stripeObject = event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new RuntimeException("stripe object null"));

        switch (event.getType()) {

            case "customer.subscription.created" -> handleCustomerSubscriptionCreated(stripeObject);

            case "customer.subscription.updated" -> handleCustomerSubscriptionUpdated(stripeObject);

            case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted(stripeObject);

            case "invoice.paid" -> handleInvoicePaid(stripeObject);

            case "invoice.payment_failed" -> handleInvoicePaymentFailed(stripeObject);
        }
    }

    @Override
    public void handleCustomerSubscriptionCreated(StripeObject stripeObject) {
        //Parsing stripe object into subscription
        //and calling subscription service to create new subscription entity in the DB
        subscriptionService.createSubscription((Subscription) stripeObject);
    }

    @Override
    public void handleCustomerSubscriptionUpdated(StripeObject stripeObject) {
        subscriptionService.updateSubscription((Subscription) stripeObject);
    }

    @Override
    public void handleCustomerSubscriptionDeleted(StripeObject stripeObject) {
        subscriptionService.cancelSubscription((Subscription) stripeObject);
    }

    @Override
    public void handleInvoicePaid(StripeObject stripeObject) {
        paymentHistoryService.record((Invoice) stripeObject);
    }

    @Override
    public void handleInvoicePaymentFailed(StripeObject stripeObject) {
        paymentHistoryService.record((Invoice) stripeObject);
    }

}