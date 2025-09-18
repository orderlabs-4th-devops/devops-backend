package org.example.groworders.domain.payment.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;
import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.orders.model.entity.Order;
import org.example.groworders.domain.payment.model.entity.PaymentEntity;
import org.example.groworders.domain.payment.model.entity.PaymentStatus;
import org.example.groworders.domain.users.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentDto {

    // 결제 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long orderId;               // 주문 ID
        private Long userId;                // 사용자 ID
        private Long totalAmount;            // 주문 총 결제 금액
        private String impUid;              // PortOne 결제 UID
        private List<Long> cropOrderMgmtList;   // 주문한 상품 관리 ID 리스트
        private String paymentId;
        private String txId;
    }

    // 결제 완료 후 개별 상품 결제 내역 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemResponse {
        private Long paymentId;
        private Long orderId;
        private Long userId;
        private String productName;
        private String productOption; // 옵션 문자열 (색상, 사이즈 등)
        private Long price;        // 개별 상품 가격
        private Long totalAmount;     // 주문 총 결제 금액
        private LocalDateTime paidAt;
        private Boolean review;       // 리뷰 작성 여부
    }

    // 결제 내역 조회용 DTO (리스트 반환용)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class History {
        private Long paymentId;
        private String productName;
        private String productOption;
        private Long price;
        private Long totalAmount;
        private LocalDateTime paidAt;
        private Boolean review;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Save {
        private Long orderId;
        private Long userId;
        private Long totalAmount;
        private List<Long> cropOrderMgmtList;
        private String impUid;
        private String payMethod;
        private String bankCode;
        private String bankName;
        private String buyerAddr;
        private String buyerEmail;
        private String productName;
        private String productOption;
        private Integer price;
        private Integer totalPrice;
        private Long quantity;
        private PaymentStatus statusType;

        public PaymentEntity toEntity(User user, Order order, Crop crop) {
            return PaymentEntity.builder()
                    .user(user)
                    .order(order)
                    .crop(crop)
                    .impUid(this.impUid)
                    .payMethod(this.payMethod)
                    .bankCode(this.bankCode)
                    .bankName(this.bankName)
                    .buyerAddr(this.buyerAddr)
                    .buyerEmail(this.buyerEmail)
                    .productName(this.productName)
                    .productOption(this.productOption)
                    .price(this.price)
                    .totalPrice(this.totalPrice)
                    .paidAt(LocalDateTime.now()) // 현재 시간 저장
                    .statusType(this.statusType)
                    .review(false)
                    .quantity(this.quantity)
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Validation {
        private String paymentId;
        private Long orderId; // 추가
    }
}
