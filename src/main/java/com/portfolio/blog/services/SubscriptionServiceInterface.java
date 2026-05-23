package com.portfolio.blog.services;

import com.stripe.model.Subscription;

public interface SubscriptionServiceInterface {
    void createSubscription(Subscription subscription);
    void updateSubscription(Subscription subscription);
    void cancelSubscription(Subscription subscription);
}
