package org.example.groworders.config.push.model.entity;

public enum PushType {
    ORDER_REGISTER_NOTIFICATION("주문 등록 알림"),
    ORDER_STATUS_UPDATE_NOTIFICATION("주문 상태 변경 알림");

    PushType(String s) {
    }
}
