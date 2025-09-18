package org.example.groworders.domain.orders.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.groworders.domain.orders.model.dto.OrderDto;
import org.example.groworders.domain.payment.model.entity.PayMethod;
import org.example.groworders.domain.payment.model.entity.PaymentStatus;
import org.example.groworders.domain.users.model.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter @Setter
@AllArgsConstructor
@Table(name = "orders")
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private User user;

    private String orderId;
    private String ordererName;

    private String productName;

    @Enumerated(EnumType.STRING)
    PayMethod payMethod;

    @Column(length = 100)
    private String merchantUid;

    private Long totalPrice;

    private String address;

    private String detailAddress;

    private String postCode;

    private String phoneNumber;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime orderDay;

    private PaymentStatus paymentStatus;

//    @OneToMany(mappedBy = "orders")
//    private List<Payment> paymentHistories = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void orderConfirm(String merchantUid, OrderDto.Confirm orderDto) {
        this.merchantUid = merchantUid;
        this.postCode = orderDto.getPostCode();
        this.address = orderDto.getAddress();
        this.detailAddress = orderDto.getDetailAddress();
        this.ordererName = orderDto.getOrdererName();
        this.phoneNumber = orderDto.getPhoneNumber();
        this.payMethod = orderDto.getPayMethod();
        this.orderDay = LocalDateTime.now();

    }
}
