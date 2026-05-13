package com.portfolio.blog.services;

import com.stripe.model.Event;

public interface StripeEventHandlerServiceInterface {

    void handle(Event event);

    void handleCustomerSubscriptionCreated(Event event);

    void handleCustomerSubscriptionUpdated(Event event);

    void handleCustomerSubscriptionDeleted(Event event);

    void handleInvoicePaid(Event event);

    void handleInvoicePaymentFailed(Event event);

}
