package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.payment.CreateProductRequest;
import com.portfolio.blog.domain.dto.payment.ProductResponse;
import com.portfolio.blog.services.PaymentServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/payment")
public class PaymentController {

    private final PaymentServiceInterface paymentService;

    public PaymentController(PaymentServiceInterface paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping(path = "/products")
    public ResponseEntity<List<ProductResponse>> get() {

        return ResponseEntity.ok().body(paymentService.getProducts());
    }

    @PostMapping(path = "/products")
    public ResponseEntity<ProductResponse> create(
            @RequestBody @Valid CreateProductRequest productRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createProduct(productRequest));
    }

}
