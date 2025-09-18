package org.example.groworders.domain.orders.model.entity;

import jakarta.persistence.Entity;
import lombok.*;

import jakarta.persistence.*;
import org.example.groworders.domain.cart.model.entity.Cart;
import org.example.groworders.domain.crops.model.entity.Crop;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CropOrderManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 정보
    @ManyToOne
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Long price;

    @OneToMany(mappedBy = "cropOrderManagement")
    private List<Cart> carts = new ArrayList<>();
}
