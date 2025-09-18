package org.example.groworders.domain.orders.repository;

import org.example.groworders.domain.orders.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByMerchantUid(String merchantUid);
}
