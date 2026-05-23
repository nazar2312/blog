package com.portfolio.blog.services;

import com.stripe.model.Event;
import com.stripe.model.StripeObject;

public interface StripeEventHandlerServiceInterface {

    void handle(Event event);

    void handleCustomerSubscriptionCreated(StripeObject stripeObject);

    void handleCustomerSubscriptionUpdated(StripeObject stripeObject);

    void handleCustomerSubscriptionDeleted(StripeObject stripeObject);

    void handleInvoicePaid(StripeObject stripeObject);

    void handleInvoicePaymentFailed(StripeObject stripeObject);
}
