package org.example.groworders.domain.payment.repository;

import org.example.groworders.domain.payment.model.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity,Long> {
    List<PaymentEntity> findByImpUid(String impUid);
}
