package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.SubscriptionEntity;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.exceptions.ResourceNotFoundException;
import com.portfolio.blog.repositories.SubscriptionRepository;
import com.portfolio.blog.services.SubscriptionServiceInterface;
import com.portfolio.blog.services.UserServiceInterface;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService implements SubscriptionServiceInterface {

    private final UserServiceInterface userService;
    private final SubscriptionRepository repository;

    @Override
    @Transactional
    public void createSubscription(Subscription subscription) {

        UserEntity user = userService.getUserByCustomerId(subscription.getCustomer());

        SubscriptionEntity entity = SubscriptionEntity.builder()
                .user(user)
                .stripeSubscriptionId(subscription.getId())
                .status(subscription.getStatus())
                .created(Instant.ofEpochSecond(subscription.getCreated()))
                .currentPeriodStart(Instant.ofEpochSecond(subscription.getStartDate()))
                .currentPeriodEnd(Instant.ofEpochSecond(subscription.getItems().getData().getFirst().getCurrentPeriodEnd()))
                .build();

        repository.save(entity);
        log.info("User '{}' subscribed for premium", user.getUsername());
    }

    @Override
    @Transactional
    public void updateSubscription(Subscription subscription) {

        SubscriptionEntity entity = repository.findByStripeSubscriptionId(subscription.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription is not found"));

        // Checking if status was changed
        if (!subscription.getStatus().equals(entity.getStatus())) {

            entity.setStatus(subscription.getStatus());
            log.info("Status '{}' was set for the subscription - {} ",
                    entity.getStatus(),
                    entity.getStripeSubscriptionId()
            );
        }

        // If subscription was renewed setting new start/end dates.
        if (subscription.getItems().getData().getFirst().getCurrentPeriodStart() != entity.getCurrentPeriodStart().getEpochSecond()) {

            entity.setCurrentPeriodStart(Instant.ofEpochSecond(
                    subscription.getItems().getData().getFirst().getCurrentPeriodStart())
            );
            entity.setCurrentPeriodEnd(Instant.ofEpochSecond(
                    subscription.getItems().getData().getFirst().getCurrentPeriodEnd())
            );

            log.info("Current period start/end was updated for the subscription - {}", entity.getStripeSubscriptionId());
        }
        repository.save(entity);
    }

    @Override
    @Transactional
    public void cancelSubscription(Subscription subscription) {

        SubscriptionEntity entity = repository.findByStripeSubscriptionId(subscription.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription is not found"));

        repository.delete(entity);
        log.info( "User '{}' canceled premium" , entity.getUser().getUsername());
    }
}
