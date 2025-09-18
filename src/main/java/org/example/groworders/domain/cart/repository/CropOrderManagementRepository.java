package org.example.groworders.domain.cart.repository;

import org.example.groworders.domain.orders.model.entity.CropOrderManagement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CropOrderManagementRepository extends JpaRepository<CropOrderManagement,Long> {
}
