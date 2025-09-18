package org.example.groworders.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.cart.model.dto.CartDto;
import org.example.groworders.domain.cart.model.entity.Cart;
import org.example.groworders.domain.orders.model.entity.CropOrderManagement;
import org.example.groworders.domain.cart.repository.CartRepository;
import org.example.groworders.domain.cart.repository.CropOrderManagementRepository;
import org.example.groworders.domain.crops.repository.CropRepository;
import org.example.groworders.domain.users.model.entity.User;
import org.example.groworders.domain.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    //    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final CropOrderManagementRepository cropOrderManagementRepository;

    /**
     * 장바구니 담기
     */
    public Long addCart(CartDto.Request request, Long cropOrdMgtId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);
        // 회원 정보
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));

        // 상품 정보
        CropOrderManagement cropOrdMgt = cropOrderManagementRepository.findById(cropOrdMgtId)
                .orElseThrow(() -> new NoSuchElementException("상품이 존재하지 않습니다. cropOrdMgtId: " + cropOrdMgtId));

        // 장바구니 기존 여부 확인
        Cart existingCart = cartRepository.findByCropOrderManagementAndUser(cropOrdMgt, user).orElse(null);

        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + request.getQuantity());
            existingCart.setPrice(existingCart.getPrice() + (long) cropOrdMgt.getPrice() * request.getQuantity());
            cartRepository.save(existingCart);
            return existingCart.getId();
        } else {
            Long price = (long) cropOrdMgt.getPrice() * request.getQuantity();
            Cart cart = Cart.builder()
                    .user(user)
                    .cropOrderManagement(cropOrdMgt)
                    .price(price)
                    .quantity((long) request.getQuantity())
                    .build();
            cartRepository.save(cart);
            return cart.getId();
        }
    }

    /**
     * 내 장바구니 리스트
     */
    @Transactional(readOnly = true)
    public List<CartDto.Status> allCarts(String id) {
        System.out.println("id = " + id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다."));
        List<Cart> carts = cartRepository.findAll().stream()
                .filter(cart -> cart.getUser().getId().equals(user.getId()))
                .toList();
        return carts.stream()
                .map(CartDto.Status::fromEntity)
                .collect(Collectors.toList());
    }
}
