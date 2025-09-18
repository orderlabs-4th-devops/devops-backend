package org.example.groworders.domain.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.cart.model.dto.CartDto;
import org.example.groworders.domain.cart.service.CartService;
import org.example.groworders.domain.users.model.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "장바구니 기능")
public class CartController {
    private final CartService cartService;

    /**
     * 장바구니 담기
     */
    @Operation(
            summary = "장바구니에 등록 -구매자만 가능",
            description = "장바구니 생성 버튼 눌렀을 때 입력값을 받아 장바구니에 등록함"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 등록 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "장바구니에 등록되었습니다. cart_id : 1")))
    })
    @PostMapping("/add/{cropMgtId}")
    public ResponseEntity<String> addCart(
            @Valid @RequestBody CartDto.Request request,
            @Parameter(description = "상품 관리 ID", required = true, example = "1031")@PathVariable Long cropMgtId,
            @AuthenticationPrincipal String userId
    ) {
        System.out.println(userId);
        Long createdId = cartService.addCart(request, cropMgtId);
        return ResponseEntity.ok("장바구니에 등록되었습니다. cart_id : " + createdId);
    }

    /**0
     * 내 장바구니 리스트
     */
    @Operation(
            summary = "장바구니 조회 -구매자만 가능",
            description = "장바구니에 담았던 장바구니 목록을 전체 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 조회 성공")

    })
    @GetMapping("/{userId}")
    public List<CartDto.Status> getMyCarts(@PathVariable Long userId) {
        return cartService.allCarts(String.valueOf(userId));
    }
}
