package org.example.groworders.domain.cart.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.groworders.domain.cart.model.entity.Cart;

public class CartDto {
    @Getter
    @Builder
    public static class Status {
        @Schema(description = "장바구니 ID", example = "1")
        private Long id;
        @Schema(description = "사용자 ID", example = "1")
        private Long userId;
        @Schema(description = "사용자 이름", example = "tester")
        private String userName;
        @Schema(description = "작물 종류", example = "토마토")
        private String cropType;
        @Schema(description = "수량", example = "30")
        private Long quantity;
        @Schema(description = "총 가격", example = "36000")
        private Long totalPrice;

        public static Status fromEntity(Cart entity) {
            return Status.builder()
                    .id(entity.getId())
                    .userId(entity.getUser().getId())
                    .userName(entity.getUser().getName())
                    .cropType(entity.getCropOrderManagement().getCrop().getType())
                    .quantity(entity.getQuantity())
                    .totalPrice(entity.getPrice())
                    .build();
        }
    }
    @Schema(name="CartRequest")
    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private Long cropOrderManagementId;
        @Schema(description = "수량", required = true, example = "3")
        private int quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {   // 응답용 DTO
        private Long Id;
        private String message;
    }
}
