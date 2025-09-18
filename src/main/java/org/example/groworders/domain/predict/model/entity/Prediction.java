package org.example.groworders.domain.predict.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cropName;
    private String cultivationType;
    private String growthStage;
    private Double cumulativeSolarMin;
    private Double cumulativeSolarMax;
    private Double outsideTempMin;
    private Double outsideTempMax;
    private Double yield;
    private Double weeklyAvgHumidityMin;
    private Double weeklyAvgHumidityMax;
}