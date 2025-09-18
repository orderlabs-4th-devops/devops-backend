package org.example.groworders.domain.payment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.orders.model.entity.CropOrderManagement;
import org.example.groworders.domain.orders.model.entity.Order;
import org.example.groworders.domain.users.model.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Order order;

    @ManyToOne
    @JoinColumn
    private Crop crop;

    private String impUid;
    private String merchantUid;

    private String payMethod;

    private String bankCode;
    private String bankName;
    private String buyerAddr;
    private String buyerEmail;

    private String productName;
    private String productOption;

    private Integer price;
    private Integer totalPrice;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus statusType;

    private Boolean review = false;

    private Long quantity;


}