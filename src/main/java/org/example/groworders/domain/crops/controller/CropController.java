package org.example.groworders.domain.crops.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.*;
import org.example.groworders.common.model.BaseResponse;
import org.example.groworders.domain.crops.model.dto.CropDto;
import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.crops.service.CropService;
import org.example.groworders.domain.inventories.model.dto.InventoryDto;
import org.example.groworders.domain.inventories.service.InventoryService;
import org.example.groworders.domain.predict.model.dto.PredictionDto;
import org.example.groworders.domain.predict.service.PredictionService;
import org.example.groworders.domain.users.model.dto.UserDto;
import org.example.groworders.domain.weather.model.dto.WeatherDto;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crops")
@Tag(name = "작물 관리 기능")
public class CropController {
    private final CropService cropService;
    private final InventoryService inventoryService;
    private final PredictionService predictionService;
    private final RestTemplate restTemplate;

    //작물 등록과 동시에 재고 등록
    @Operation(
            summary = "작물 등록과 동시에 재고 등록",
            description = "작물 타입, 작물 상태, 재배 면적, 파종 시작일, 재배 방식, 농장 아이디를 입력 받고 작물을 등록함과 동시에 예측 재고를 등록 한다."
    )
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Object>> register(
            @Valid @RequestBody CropDto.Register dto,
            @AuthenticationPrincipal UserDto.AuthUser authUser
    ) throws ChangeSetPersister.NotFoundException {
        /** 작물 등록 */
        Crop crop = cropService.register(dto);

        /** 예측 생산량 직접 호출 */
        WeatherDto.WeatherData weather = restTemplate.getForObject("https://www.be17.site/api/weather", WeatherDto.WeatherData.class); // Weather API 호출
        PredictionDto.RequestDaily requestDaily = cropService.transform(crop);
        PredictionDto.Response prediction = predictionService.predictDaily(requestDaily.getCropName(), requestDaily.getCultivationType(), requestDaily.getGrowthStage(), weather);

        /** 재고 등록 */
        InventoryDto.Register inventoryDTO = cropService.transform(crop, prediction);
        InventoryDto.InventoryResponse result = inventoryService.save(inventoryDTO, authUser.getId());

        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
