package org.example.groworders.domain.orders.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.groworders.domain.cart.model.dto.CartDto;
import org.example.groworders.domain.orders.model.entity.Order;
import org.example.groworders.domain.payment.model.entity.PayMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

import lombok.*;
import org.example.groworders.domain.payment.model.entity.PaymentStatus;
import org.example.groworders.domain.users.model.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String postCode;
        private String address;
        private String detailAddress;
        private String ordererName;
        private String phoneNumber;
        private PayMethod payMethod;

        // Confirm 변환 메서드
        public Confirm toConfirm() {
            return Confirm.builder()
                    .postCode(this.postCode)
                    .address(this.address)
                    .detailAddress(this.detailAddress)
                    .ordererName(this.ordererName)
                    .phoneNumber(this.phoneNumber)
                    .payMethod(this.payMethod)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Confirm {
        @Schema(description = "우편번호", example = "39021")
        private String postCode;
        @Schema(description = "주소", example = "충청북도 증평읍")
        private String address;
        @Schema(description = "세부 주소", example = "삼일로 82")
        private String detailAddress;
        @Schema(description = "주문자 성명", example = "심시경")
        private String ordererName;
        @Schema(description = "전화번호", example = "010-3258-3122")
        private String phoneNumber;
        @Schema(description = "결제수단", example = "카카오 결제")
        private PayMethod payMethod;

        // Entity 변환 메서드
        public Order toEntity(User user, String merchantUid) {
            return Order.builder()
                    .user(user)
                    .merchantUid(merchantUid)
                    .postCode(this.postCode)
                    .address(this.address)
                    .detailAddress(this.detailAddress)
                    .ordererName(this.ordererName)
                    .phoneNumber(this.phoneNumber)
                    .payMethod(this.payMethod)
                    .orderDay(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String orderId;
        private String merchantUid;
        private String ordererName;
        private String address;
        private String detailAddress;
        private String postCode;
        private String phoneNumber;
        private PayMethod payMethod;
        private Long totalPrice;
        private LocalDateTime orderDay;
        private PaymentStatus paymentStatus;

        // 엔티티 → Response 변환
        public static Response fromEntity(Order order) {
            return Response.builder()
                    .orderId("1")
                    .merchantUid(order.getMerchantUid())
                    .ordererName(order.getOrdererName())
                    .address(order.getAddress())
                    .detailAddress(order.getDetailAddress())
                    .postCode(order.getPostCode())
                    .phoneNumber(order.getPhoneNumber())
                    .payMethod(order.getPayMethod())
                    .totalPrice(order.getTotalPrice())
                    .orderDay(order.getOrderDay())
                    .paymentStatus(order.getPaymentStatus())
                    .build();
        }
    }
}
