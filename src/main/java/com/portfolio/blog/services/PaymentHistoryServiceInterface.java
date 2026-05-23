package com.portfolio.blog.services;

import com.stripe.model.Invoice;

public interface PaymentHistoryServiceInterface {
    void record(Invoice invoice);
}
