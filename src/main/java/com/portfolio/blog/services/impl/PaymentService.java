package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.payment.CreateProductRequest;
import com.portfolio.blog.domain.dto.payment.ProductResponse;
import com.portfolio.blog.services.PaymentServiceInterface;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PaymentService implements PaymentServiceInterface {

    private final StripeClient stripeClient;

    public PaymentService(@Value("${stripe.secret.test}") String key) {
        this.stripeClient = new StripeClient(key);
    }

    @Override
    public List<ProductResponse> getProducts() {

        try {
            // Calling stripe API to get products, then converting them to ProductResponse DTO.
            List<ProductResponse> products = stripeClient.v1().products().list().getData().stream()
                    .map(product -> ProductResponse.builder()
                            .id(product.getId())
                            .active(product.getActive())
                            .name(product.getName())
                            .build()
                    )
                    .toList();

            return products;

        } catch (StripeException ex) {
            log.warn("Exception occurred - " + ex.getMessage());
        }
        return List.of();
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest productRequest) {
        try {
            Product product = stripeClient.v1().products().create(
                    ProductCreateParams.builder()
                            .setName(productRequest.getName())
                            .build()
            );
            stripeClient.v1().prices().create(
                    PriceCreateParams.builder()
                            .setCurrency(productRequest.getCurrency())
                            .setUnitAmount(productRequest.getAmount())
                            .setRecurring(
                                    PriceCreateParams.Recurring.builder()
                                            .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                            .build())
                            .setProduct(product.getId())
                            .build()
            );
            return ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .active(product.getActive())
                    .build();

        } catch (StripeException e) {
            log.error("Failed to create product, " + e.getMessage());
            throw new RuntimeException("Failed to create product");
        }

    }

}
