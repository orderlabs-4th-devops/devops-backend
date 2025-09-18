package org.example.groworders.domain.orders.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.orders.model.dto.OrderDto;
import org.example.groworders.domain.orders.model.entity.Order;
import org.example.groworders.domain.orders.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "주문 기능")
public class OrderController {
    private final OrderService orderService;
    private final HttpSession httpSession;

    @Operation(
            summary = "장바구니 체크 표시 된 것만 주문 생성",
            description = "장바구니안 상품 목록조회 한 것 중에 구매자가 원하는 주문만 선택하여 주문 진행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 등록 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "주문 임시 저장 성공했습니다")))
    })
    // 임시 주문 테이블 생성
    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody Map<String, Object> payload) {
        List<Integer> cartIdsInteger = (List<Integer>) payload.get("cartIds");
        List<Long> cartIds = cartIdsInteger.stream().map(Long::valueOf).collect(Collectors.toList());
        Order temporaryOrder = orderService.createOrder(cartIds);

        // 세션에 임시 주문 정보를 저장
        httpSession.setAttribute("temporaryOrder", temporaryOrder);
        httpSession.setAttribute("cartIds", cartIds); // 장바구니 id 저장

        return ResponseEntity.ok("주문 임시 저장 완료");
    }

    @Operation(
            summary = "장바구니에 선택된 상품 주문 확정",
            description = "결제 진행 전, 장바구니에서 선택된 상품으로 임시 생성된 주문을 최종 확정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 확정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "주문 확정 성공했습니다")))
    })
    // 최종 주문 테이블 생성
    @PostMapping("/confirm")
    public ResponseEntity<Object> completeOrder(@Valid @RequestBody OrderDto.Request request) {

        // Request → Confirm 변환
        OrderDto.Confirm confirmDto = request.toConfirm();

        // 세션에서 임시 주문 정보 가져오기
        Order temporaryOrder = (Order) httpSession.getAttribute("temporaryOrder");

        if (temporaryOrder == null) {
            return ResponseEntity.badRequest().body("임시 주문 정보를 찾을 수 없습니다.");
        }

        // 서비스 호출
        Order completedOrder = orderService.orderConfirm(temporaryOrder, confirmDto);

        // Entity → Response 변환
        OrderDto.Response responseDto = OrderDto.Response.fromEntity(completedOrder);

        return ResponseEntity.ok(responseDto);
    }



}