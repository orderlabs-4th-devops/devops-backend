package org.example.groworders.domain.inventories.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.*;
import org.example.groworders.common.model.BaseResponse;
import org.example.groworders.domain.crops.model.dto.CropDto;
import org.example.groworders.domain.farms.model.dto.FarmDto;
import org.springframework.web.bind.annotation.*;
import org.example.groworders.domain.inventories.model.dto.InventoryDto;
import org.example.groworders.domain.inventories.service.InventoryService;
import org.springframework.http.ResponseEntity;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
@Tag(name = "재고 관리 기능")
public class InventoryController {
    private final InventoryService inventoryService;

/*
    //재고 등록
    @Operation(
            summary = "재고 등록",
            description = "농부의 각 농장에 있는 작물들에 대한 예측 재고 등록 : 예측 수확일, 예측 수확량, 최대 예측 수확량, 작물 아이디")
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Object>> register(@Valid @RequestBody InventoryDto.Register dto) {
        inventoryService.save(dto);
        return ResponseEntity.ok(BaseResponse.successMessage("재고 등록 성공"));
    }
*/

    //재고 수정
    @Operation(
            summary = "예측 재고 수정",
            description = "예측 재고 생산량, 예측 수확일, 파종 시작일을 입력 받고 농부의 각 농장에 있는 작물에 대한 예측 재고 수정한다.")
    @PostMapping("/update")
    public ResponseEntity<BaseResponse<Object>> update(@Valid @RequestBody InventoryDto.Update dto) {
        inventoryService.update(dto);
        return ResponseEntity.ok(BaseResponse.successMessage("재고 수정 성공"));
    }

    //재고 상세 조회
    @Operation(
            summary = "예측 재고 상세 조회",
            description = "parameter로 작물 및 재고 아이디를 전달 받아 예측 재고를 상세 조회")
    @GetMapping("/details")
    public ResponseEntity<BaseResponse<CropDto.CropResponse>> details(Long cropId) {
        CropDto.CropResponse result = inventoryService.details(cropId);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    //재고 목록 조회
    @Operation(
            summary = "예측 재고 목록 조회",
            description = "parameter로 농장 아이디를 전달 받아 농부가 소유한 농장의 재고 목록 조회")
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<FarmDto.FarmListResponse>> list(Long farmId, Integer page, Integer size) {
        FarmDto.FarmListResponse result = inventoryService.list(farmId, page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    //재고 검색 조회
    @Operation(
            summary = "예측 재고 목록 필터 조회",
            description = "작물 종류, 작물 상태, 판매 상태, 정렬 기준 를 각각 선택할 수 있으며 해당하는 재고만 조회")
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<CropDto.CropResponse>>> search(Long farmId, CropDto.Search dto) {
        List<CropDto.CropResponse> result = inventoryService.search(farmId, dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
