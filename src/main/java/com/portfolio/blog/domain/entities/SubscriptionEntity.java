package com.portfolio.blog.domain.entities;

import com.portfolio.blog.domain.entities.enums.SubscriptionCollectionMethod;
import com.portfolio.blog.domain.entities.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity productId;

    @Column(name = "stripe_subscription_id", nullable = false)
    private String stripeSubscriptionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private SubscriptionCollectionMethod collectionMethod;

    @Column
    private LocalDateTime currentPeriodStart;

    @Column
    private LocalDateTime currentPeriodEnd;

    @Column
    private LocalDateTime cancelAt;

    @Column
    private LocalDateTime canceledAt;

    @Column
    private LocalDateTime endedAt;
}
