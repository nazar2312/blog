package com.portfolio.blog.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "stripe_product_id", nullable = false)
    private String stripeProductId;

    @Column(name = "stripe_price_id", nullable = false)
    private String stripePriceId;

}
