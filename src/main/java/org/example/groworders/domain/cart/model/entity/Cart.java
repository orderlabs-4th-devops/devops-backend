package org.example.groworders.domain.cart.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.groworders.common.model.BaseEntity;
import org.example.groworders.domain.orders.model.entity.CropOrderManagement;
import org.example.groworders.domain.users.model.entity.User;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 회원

    @ManyToOne
    @JoinColumn(name = "crop_order_management_id", nullable = false)
    private CropOrderManagement cropOrderManagement; // 상품

    @Column(nullable = false)
    private Long quantity; // 수량

    @Column(nullable = false)
    private Long price; // 가격
}