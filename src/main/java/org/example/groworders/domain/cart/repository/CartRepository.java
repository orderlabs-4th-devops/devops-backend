package org.example.groworders.domain.cart.repository;

import org.example.groworders.domain.cart.model.entity.Cart;
import org.example.groworders.domain.orders.model.entity.CropOrderManagement;
import org.example.groworders.domain.users.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCropOrderManagementAndUser(CropOrderManagement cropOrdMgt, User user);
    List<Cart> findByIdIn(List<Long> id);
    Cart findByCropOrderManagement(CropOrderManagement cropOrderManagement);
}
