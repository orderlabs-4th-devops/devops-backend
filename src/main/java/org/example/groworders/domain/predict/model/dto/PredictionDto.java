package org.example.groworders.domain.predict.model.dto;

import lombok.*;
import org.example.groworders.domain.predict.model.entity.Prediction;
import org.example.groworders.domain.weather.model.dto.WeatherDto;
import org.example.groworders.domain.weather.model.entity.Weather;

import java.time.LocalDate;
import java.util.List;

public class PredictionDto {

    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDaily {
        private String cropName;
        private String cultivationType;
        private String growthStage;
        private WeatherDto.WeatherData weatherData;

        // WeatherData → Weather Entity 변환
        public Weather toEntity(double predictedYield) {
            return Weather.builder()
                    .tm(weatherData.getTm())
                    .stn(weatherData.getStn())
                    .ws(weatherData.getWs())
                    .ta(weatherData.getTa())
                    .hm(weatherData.getHm())
                    .rn(weatherData.getRn())
                    .si(weatherData.getSi())
                    .sowingDate(LocalDate.now())
                    .predictYield(String.valueOf(predictedYield))
                    .build();
        }
    }

    @Setter
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestYearly {
        private String cropName;
        private String cultivationType;
        private String growthStage;
        private List<WeatherDto.WeatherData> weatherDataList;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String cropName;
        private String cultivateType;
        private String growthStage;
        private BestMatch bestMatch;
        private String predictedYield;

        public static Response fromEntity(Prediction prediction, WeatherDto.WeatherData inputData, double predictedYield) {
            return Response.builder()
                    .cropName(prediction.getCropName())
                    .cultivateType(prediction.getCultivationType())
                    .growthStage(prediction.getGrowthStage())
                    .bestMatch(BestMatch.builder()
                            .tm(inputData.getTm())
                            .stn(inputData.getStn())
                            .siMax(prediction.getCumulativeSolarMax())
                            .siMin(prediction.getCumulativeSolarMin())
                            .taMax(prediction.getOutsideTempMax())
                            .taMin(prediction.getOutsideTempMin())
                            .yield(prediction.getYield())
                            .build())
                    .predictedYield(String.valueOf(predictedYield))
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BestMatch {
        private String tm;
        private String stn;
        private Double taMax;
        private Double taMin;
        private Double siMax;
        private Double siMin;
        private Double yield;
    }
}
