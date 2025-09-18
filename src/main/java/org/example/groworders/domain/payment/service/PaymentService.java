package org.example.groworders.domain.payment.service;

import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.orders.model.entity.Order;
import org.example.groworders.domain.orders.model.entity.OrderItem;
import org.example.groworders.domain.orders.model.entity.OrderStatus;
import org.example.groworders.domain.orders.repository.OrderRepository;
import org.example.groworders.domain.payment.model.dto.PaymentDto;
import org.example.groworders.domain.payment.model.entity.PaymentEntity;
import org.example.groworders.domain.payment.model.entity.PaymentStatus;
import org.example.groworders.domain.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    // NOTE: 실제 운영에서는 환경변수/설정파일로 주입하세요.
    private static final String PORTONE_API_KEY = "kx10B9YDj3Z8TvU9XPq0ZZxhNDbaNff6VQMBF3Q1lAoHvTAnZz5jKnrQj5gqDA1UrxLBtojunmsJ61w1";
    private static final String PORTONE_BASE_URL = "https://api.portone.io";
    private static final String PORTONE_STORE_ID = null;

    @Transactional
    public boolean validation(PaymentDto.Validation dto) throws SQLException, IOException {
        // 1) PortOne 클라이언트 생성
        PaymentClient paymentClient = new PaymentClient(
                PORTONE_API_KEY,
                PORTONE_BASE_URL,
                PORTONE_STORE_ID
        );

        System.out.println(dto.getPaymentId());

        // 2) 결제 조회
        CompletableFuture<Payment> completableFuture = paymentClient.getPayment(dto.getPaymentId());
        Payment payment = completableFuture.join();
//
//        // 3) 결제 완료 여부 확인 및 금액 대조
        if (payment instanceof PaidPayment paidPayment) {
            int paidAmount = Math.toIntExact(paidPayment.getAmount().getTotal());

            // 주문 로드
            Order order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new NoSuchElementException("Order not found: " + dto.getOrderId()));

            int totalPrice = order.getOrderItems().stream()
                    .mapToInt(OrderItem::getPrice)
                    .sum();

            if (paidAmount != totalPrice) {
                // 금액 상이
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);
                return false;
            }

            // 4) 주문 상태 변경
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);

            // 5) 결제내역 저장 (주문 아이템 기준으로 저장)
            List<PaymentEntity> toSave = new ArrayList<>();
            for (OrderItem item : order.getOrderItems()) {
                PaymentEntity entity = PaymentEntity.builder()
                        .order(order)
                        .user(order.getUser())
                        .impUid(dto.getPaymentId()) // PortOne paymentId를 impUid로 저장
                        .productName(item.getCropOrderManagement().getCrop().getType())
                        .price(item.getPrice())
                        .totalPrice(totalPrice)
                        .paidAt(LocalDateTime.now())
                        .statusType(PaymentStatus.PENDING)
                        .quantity((long) item.getQuantity())
                        .build();
                toSave.add(entity);
            }
            if (!toSave.isEmpty()) {
                paymentRepository.saveAll(toSave);
            }

            return true;
        }

        return false;
    }
}
