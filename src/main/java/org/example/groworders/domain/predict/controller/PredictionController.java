package org.example.groworders.domain.predict.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.predict.model.dto.PredictionDto;
import org.example.groworders.domain.predict.service.PredictionService;
import org.example.groworders.domain.weather.model.dto.WeatherDto;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/predict")
@RequiredArgsConstructor
@Tag(name = "생산량 예측 기능")
public class PredictionController {

    private final PredictionService predictionService;
    private final RestTemplate restTemplate;

    // -------------------------------
    // 하루치 예측
    // -------------------------------
    @Operation(
            summary = "하루 생산량 예측 기능",
            description = "기상청 API를 통해 날씨를 입력받고 오차를 통해 생산량 예측"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "하루 생산량 예측 기능 성공")

    })
    @PostMapping("/daily")
    public ResponseEntity<PredictionDto.Response> predictDaily(@RequestBody PredictionDto.RequestDaily request) {
        try {
            // Weather API 호출
            WeatherDto.WeatherData weather = restTemplate.getForObject("https://www.be17.site/api/weather", WeatherDto.WeatherData.class);
            PredictionDto.Response response = predictionService.predictDaily(
                    request.getCropName(),
                    request.getCultivationType(),
                    request.getGrowthStage(),
                    weather
            );
            return ResponseEntity.ok(response);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // -------------------------------
    // 연속일수 예측
    // -------------------------------
    @Operation(
            summary = "연간 생산량 예측 기능",
            description = "전날 생산량이 오늘의 생산량에 영향을 주는 모델"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "연간 생산량 예측 기능 성공"),

    })
    @PostMapping("/continuous")
    public ResponseEntity<List<Double>> predictContinuous(@RequestBody PredictionDto.RequestYearly request) {
        try {
            List<Double> predictedYields = predictionService.predictContinuous(
                    request.getCropName(),
                    request.getCultivationType(),
                    request.getGrowthStage()
            );
            return ResponseEntity.ok(predictedYields);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}