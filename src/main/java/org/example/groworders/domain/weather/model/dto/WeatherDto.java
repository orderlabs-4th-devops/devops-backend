package org.example.groworders.domain.weather.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.groworders.domain.weather.model.entity.Weather;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class WeatherDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherData {
        @Schema(description = "관측시각", required = true, example = "202509040900")
        private String tm;   // 관측시각
        @Schema(description = "지점번호", required = true, example = "108")
        private String stn;  // 지점번호
        @Schema(description = "풍속", required = true, example = "2.6")
        private String ws;   // 풍속
        @Schema(description = "기온", required = true, example = "23.3")
        private String ta;   // 기온
        @Schema(description = "습도", required = true, example = "96.0")
        private String hm;   // 습도
        @Schema(description = "강수량", required = true, example = "43.1")
        private String rn;   // 강수량
        @Schema(description = "일사량", required = true, example = "0.16")
        private String si;   // 일사량

        public Weather toEntity(double predictedYield) {
            return Weather.builder()
                    .tm(this.tm)
                    .stn(this.stn)
                    .ws(this.ws)
                    .ta(this.ta)
                    .hm(this.hm)
                    .rn(this.rn)
                    .si(this.si)
                    .sowingDate(LocalDate.now())
                    .predictYield(String.valueOf(predictedYield))
                    .build();
        }

        public static WeatherData fromEntity(Weather weather) {
            return WeatherData.builder()
                    .tm(weather.getTm())
                    .stn(weather.getStn())
                    .ws(weather.getWs())
                    .ta(weather.getTa())
                    .hm(weather.getHm())
                    .rn(weather.getRn())
                    .si(weather.getSi())
                    .build();
        }
    }
}