package com.portfolio.blog.services;

import com.portfolio.blog.domain.entities.UserEntity;
import com.stripe.model.Customer;

public interface PaymentServiceInterface {

    String createCheckoutSession();

    Customer createCustomer(UserEntity user);
}
