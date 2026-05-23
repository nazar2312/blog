package com.portfolio.blog.services;

import com.portfolio.blog.domain.entities.UserEntity;

public interface StripeCustomerServiceInterface {

    void createCustomer(UserEntity user);

    String createCheckoutSession();

    String createBillingPortalSession();

}
