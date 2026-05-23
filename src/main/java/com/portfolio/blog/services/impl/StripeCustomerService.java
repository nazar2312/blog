package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.exceptions.ResourceNotFoundException;
import com.portfolio.blog.exceptions.StripeApiException;
import com.portfolio.blog.exceptions.UnauthenticatedException;
import com.portfolio.blog.repositories.UserRepository;
import com.portfolio.blog.services.StripeCustomerServiceInterface;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StripeCustomerService implements StripeCustomerServiceInterface {

    private final StripeClient stripeClient;
    private final UserService userService;
    private final UserRepository userRepository;

    public StripeCustomerService(
            @Value("${stripe.secret.test}") String key,
            UserService userService,
            UserRepository userRepository) {
        this.stripeClient = new StripeClient(key);
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void createCustomer(UserEntity user) {

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getUsername())
                .setEmail(user.getEmail())
                .build();
        try {
            Customer customer = stripeClient.v1().customers().create(params);
            user.setCustomerId(customer.getId());
            userRepository.save(user);

        } catch (StripeException e) {
            log.error("Failed to create new stripe customer, error message: {}", e.getMessage());
            throw new StripeApiException("Unavailable to process payments, please try again later");
        }
    }

    @Override
    public String createCheckoutSession() {

        UserEntity user = userService.getUserFromSecurityContextHolder();

        if (user == null) throw new UnauthenticatedException("Please login to subscribe");

        if (user.getCustomerId() == null) {
            createCustomer(user); //creates stripe customer and assigning customerId
        }
        SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(user.getCustomerId())
                .setSuccessUrl("http://localhost:8080/api/posts")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice("price_1TU4gqLWsxwGCC0CcdhfZsv1")
                                .build())
                .build();
        try {
            Session session = stripeClient.v1().checkout().sessions().create(sessionCreateParams);
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Failed to create stripe checkout session , error message: {}", e.getMessage());
            throw new StripeApiException("Unavailable to process payments, please try again later");
        }
    }

    @Override
    public String createBillingPortalSession() {

        UserEntity user = userService.getUserFromSecurityContextHolder();

        if (user == null) throw new UnauthenticatedException("Please login to subscribe");
        if (user.getCustomerId() == null || user.getCustomerId().isBlank())
            throw new ResourceNotFoundException("Not a customer; No subscription to manage");

        com.stripe.param.billingportal.SessionCreateParams params = com.stripe.param.billingportal.SessionCreateParams.builder()
                .setCustomer(user.getCustomerId())
                .build();
        try {
            com.stripe.model.billingportal.Session session = stripeClient.v1().billingPortal().sessions().create(params);
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Failed to create stripe billing portal session, error message: {}", e.getMessage());
            throw new StripeApiException("Unavailable to process payments, please try again later");
        }
    }
}

