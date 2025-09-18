package org.example.groworders.domain.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.weather.model.dto.WeatherDto;
import org.example.groworders.domain.weather.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "날씨 기능")
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(
            summary = "기상청 API 활용",
            description = "온도, 습도, 일사량 등 여러 요인을 활용"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "날씨 조회 성공")

    })
    @GetMapping
    public ResponseEntity<WeatherDto.WeatherData> getCurrentWeather() {
        WeatherDto.WeatherData observation = weatherService.fetchWeatherData();
        return ResponseEntity.ok(observation);
    }

}
