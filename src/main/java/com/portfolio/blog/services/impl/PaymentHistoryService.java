package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.PaymentHistoryEntity;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.repositories.PaymentHistoryRepository;
import com.portfolio.blog.services.PaymentHistoryServiceInterface;
import com.portfolio.blog.services.UserServiceInterface;
import com.stripe.model.Invoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentHistoryService implements PaymentHistoryServiceInterface {

    private final UserServiceInterface userService;
    private final PaymentHistoryRepository repository;

    @Override
    @Transactional
    public void record(Invoice invoice) {

        UserEntity user = userService.getUserByCustomerId(invoice.getCustomer()); //throws exception if not found

        PaymentHistoryEntity payment = PaymentHistoryEntity.builder()
                .stripeInvoiceId(invoice.getId())
                .status(invoice.getStatus())
                .amount(invoice.getAmountPaid())
                .user(user)
                .currency(invoice.getCurrency())
                .receiptEmail(user.getEmail())
                .created(Instant.ofEpochSecond(invoice.getCreated()))
                .build();

        repository.save(payment);
        log.info("Payment history updated");
    }
}
