package com.portfolio.blog.services;

import com.portfolio.blog.domain.entities.UserEntity;
import com.stripe.model.Customer;

public interface StripeCustomerServiceInterface {

    String checkout();

    String createCheckoutSession(Customer customer);

    Customer createCustomer(UserEntity user);

    String createBillingPortalSession(String customerId);
}
