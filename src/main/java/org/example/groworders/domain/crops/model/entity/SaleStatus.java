package org.example.groworders.domain.crops.model.entity;

import lombok.*;

@Getter
public enum SaleStatus {

    NOT_AVAILABLE("예측 재고 없음"),
    AVAILABLE("판매 중"),
    SOLD_OUT("판매 완료"),
    DISCONTINUED("폐기");

    private final String status;

    SaleStatus(String status) {
        this.status = status;
    }
}
