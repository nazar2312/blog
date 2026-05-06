package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.payment.CreateProductRequest;
import com.portfolio.blog.domain.dto.payment.ProductResponse;

import java.util.List;

public interface PaymentServiceInterface {
    List<ProductResponse> getProducts();

    ProductResponse createProduct(CreateProductRequest productRequest);
}
