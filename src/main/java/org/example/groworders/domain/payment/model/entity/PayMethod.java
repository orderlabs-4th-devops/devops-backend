package org.example.groworders.domain.payment.model.entity;

public enum PayMethod {
    CARD,       // 카드 결제
    BANK,       // 계좌 이체
    KAKAOPAY,   // 카카오페이
    NAVERPAY,   // 네이버페이
    TOSS,        // 토스페이
    EASY_PAY
}
