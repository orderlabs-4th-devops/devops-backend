package org.example.groworders.domain.weather.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // PK

    private String tm;                  // 관측시각
    private String stn;                 // 지점번호
    private String ws;                  // 풍속
    private String ta;                  // 기온
    private String hm;                  // 습도
    private String rn;                  // 강수량
    private String si;                  // 일사량
    private LocalDate sowingDate;       // 파종일
    private String predictYield;        // 예상 생산량
}