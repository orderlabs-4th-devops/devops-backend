package org.example.groworders.domain.orders.service;

import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.cart.model.dto.CartDto;
import org.example.groworders.domain.cart.model.entity.Cart;
import org.example.groworders.domain.cart.repository.CartRepository;
import org.example.groworders.domain.cart.repository.CropOrderManagementRepository;
import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.crops.repository.CropRepository;
import org.example.groworders.domain.orders.model.dto.OrderDto;
import org.example.groworders.domain.orders.model.entity.CropOrderManagement;
import org.example.groworders.domain.orders.model.entity.OrderItem;
import org.example.groworders.domain.orders.model.entity.OrderStatus;
import org.example.groworders.domain.orders.model.entity.Order;
import org.example.groworders.domain.orders.repository.OrderRepository;
import org.example.groworders.domain.users.model.entity.User;
import org.example.groworders.domain.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class OrderService {
    public final CartRepository cartRepository;
    public final OrderRepository orderRepository;
    public final CropRepository cropRepository;
    public final UserRepository userRepository;

    /**
     * 주문서 화면에 나타날 정보 (사용자에게 입력받지 않고 자동으로 가져와 화면에 띄워주거나 저장할 값)
     * @param cartIds card id 리스트
     * @return order 객체 반환
     */
    public Order createOrder(List<Long> cartIds) {
        List<Cart> carts = cartRepository.findByIdIn(cartIds);

        Long userId = carts.get(0).getUser().getId();
//        verifyUserIdMatch(userId); // 로그인 된 사용자와 요청 사용자 비교

        User user = userRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new NoSuchElementException("사용자가 없습니다 !"));

        // 주문할 상품들
        List<CropOrderManagement> productMgts = new ArrayList<>();
        for (Cart cart : carts) {
            CropOrderManagement cropOrderMgt = cart.getCropOrderManagement();
            productMgts.add(cropOrderMgt);
        }

        // 모든 장바구니의 memberID가 동일한지 확인
        boolean sameMember = carts.stream()
                .allMatch(cart -> cart.getUser().getId().equals(userId));

        if (!sameMember) {
            throw new IllegalStateException("선택한 장바구니 항목들이 동일한 사용자의 것이 아닙니다.");
        }

        // 주문 반환
        return Order.builder()
                .user(user)
                .ordererName(user.getName())
                .productName(getCropNames(carts))
                .totalPrice(calculateTotalPrice(carts))
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    // 주문 상품 이름들을 가져오는 메서드
    private String getCropNames(List<Cart> carts) {
        StringBuilder cropNamesBuilder = new StringBuilder();
        for (Cart cart : carts) {

            Long cropId = cart.getCropOrderManagement().getCrop().getId();
            Crop crop = cropRepository.findById(cropId).orElse(null);

            if (crop != null) {
                if (!cropNamesBuilder.isEmpty()) {
                    cropNamesBuilder.append(", ");
                }
                cropNamesBuilder.append(crop.getType());
            }
        }
        return cropNamesBuilder.toString();
    }

    // 회원 전화번호를 가져오는 메서드
    private String getMemberPhoneNumber(List<Cart> carts) {
        Long userId = carts.get(0).getUser().getId();
        User user = userRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        return (user != null && user.getPhoneNumber() != null) ? user.getPhoneNumber() : null;
    }

    // 총 가격을 계산하는 메서드
    private Long calculateTotalPrice(List<Cart> carts) {
        Long totalPrice = 0L;
        for (Cart cart : carts) {
            Long cartPrice = cart.getPrice();
            totalPrice += cartPrice;
        }
        return totalPrice;
    }


    // 주문 테이블 저장
    public Order orderConfirm(Order temporaryOrder, OrderDto.Confirm order) {
//        verifyUserIdMatch(temporaryOrder.getUser().getId()); // 로그인 된 사용자와 요청 사용자 비교


        String merchantUid = generateMerchantUid(); //주문번호 생성

        // 세션 주문서와 사용자에게 입력받은 정보 합치기
        temporaryOrder.orderConfirm(merchantUid, order);

        return orderRepository.save(temporaryOrder);
    }


    // 주문번호 생성 메서드
    private String generateMerchantUid() {
        // 현재 날짜와 시간을 포함한 고유한 문자열 생성
        String uniqueString = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDay = today.format(formatter).replace("-", "");

        // 무작위 문자열과 현재 날짜/시간을 조합하여 주문번호 생성
        return formattedDay +'-'+ uniqueString;
    }

    // OrderService.java 내부에 추가
    private void verifyUserIdMatch(Long userIdInRequest) {
        // SecurityContext에서 인증 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("인증 정보가 없습니다.");
        }

        // Principal에서 사용자 ID 추출 (구현 방식에 따라 다름)
        Object principal = authentication.getPrincipal();
        Long currentUserId = null;

        if (principal instanceof UserDetails) {
            // UserDetails를 구현한 CustomUser 사용 시
            User customUser = (User) principal;
            currentUserId = customUser.getId();
        } else if (principal instanceof String) {
            // username이 String으로 저장된 경우
            // userId를 추출할 수 있는 추가 로직 필요
            // 예: userRepository.findByUsername((String) principal).getId();
            throw new UnsupportedOperationException("String principal은 userId 추출을 지원하지 않습니다.");
        } else {
            throw new IllegalArgumentException("지원하지 않는 Principal 타입입니다.");
        }

        // 요청 userId와 현재 로그인 사용자 ID 비교
        if (!currentUserId.equals(userIdInRequest)) {
            throw new SecurityException("요청한 사용자와 로그인된 사용자가 일치하지 않습니다.");
        }
    }

}