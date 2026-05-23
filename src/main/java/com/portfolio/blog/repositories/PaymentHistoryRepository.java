package com.portfolio.blog.repositories;

import com.portfolio.blog.domain.entities.PaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistoryEntity, UUID> {
}
