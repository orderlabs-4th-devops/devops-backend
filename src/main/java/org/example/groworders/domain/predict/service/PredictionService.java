package org.example.groworders.domain.predict.service;

import lombok.RequiredArgsConstructor;
import org.example.groworders.domain.predict.model.dto.PredictionDto;
import org.example.groworders.domain.predict.model.entity.Prediction;
import org.example.groworders.domain.predict.repository.PredictionRepository;
import org.example.groworders.domain.weather.model.dto.WeatherDto;
import org.example.groworders.domain.weather.model.entity.Weather;
import org.example.groworders.domain.weather.repository.WeatherRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private static final double SOLAR_WEIGHT = 0.4;
    private static final double TEMP_WEIGHT = 0.4;
    private static final double HUMIDITY_WEIGHT = 0.2;
    private static final double PREVIOUS_YIELD_WEIGHT = 0.3; // 연속 예측 시 전날 영향

    private final WeatherRepository weatherRepository;
    private final PredictionRepository predictionRepository;

    // -------------------------------
    // 하루치 예측 (전날 영향 없음)
    // -------------------------------
    public PredictionDto.Response predictDaily(
            String cropName,
            String cultivationType,
            String growthStage,
            WeatherDto.WeatherData inputData
    ) throws ChangeSetPersister.NotFoundException {

        Prediction bestMatch = findBestMatchCondition(cropName, cultivationType, growthStage, inputData);

        double predictedYield = applyError(bestMatch, inputData);

        weatherRepository.save(inputData.toEntity(predictedYield));

        return PredictionDto.Response.fromEntity(bestMatch, inputData, predictedYield);
    }

    // -------------------------------
    // 연속 일수 예측 (전날 영향 반영)
    // -------------------------------
    public List<Double> predictContinuous(
            String cropName,
            String cultivationType,
            String growthStage
    ) throws ChangeSetPersister.NotFoundException {

        List<Weather> entityList = weatherRepository.findBySowingYear(2025);

        List<WeatherDto.WeatherData> weatherDataList = entityList.stream()
                .map(WeatherDto.WeatherData::fromEntity)
                .toList();

        List<Double> yields = new ArrayList<>();
        double previousYield = 0.0;

        for (WeatherDto.WeatherData weatherData : weatherDataList) {
            Prediction bestMatch = findBestMatchCondition(cropName, cultivationType, growthStage, weatherData);

            double predictedYield = applyError(bestMatch, weatherData);

            if (previousYield > 0.0) {
                predictedYield = predictedYield * (1 - PREVIOUS_YIELD_WEIGHT) + previousYield * PREVIOUS_YIELD_WEIGHT;
            }

            yields.add(predictedYield);
            previousYield = predictedYield;
        }

        return yields;
    }

    // -------------------------------
    // 공통: 가장 유사한 조건 찾기
    // -------------------------------
    private Prediction findBestMatchCondition(
            String cropName,
            String cultivationType,
            String growthStage,
            WeatherDto.WeatherData inputData
    ) throws ChangeSetPersister.NotFoundException {

        List<Prediction> conditions = predictionRepository.findByCropAndTypeAndStage(cropName, cultivationType, growthStage);
        if (conditions.isEmpty()) throw new ChangeSetPersister.NotFoundException();

        double solarRadiation = parseDoubleSafe(inputData.getSi()) * 1000; // MJ/m² → J/cm²/day
        double temperature = parseDoubleSafe(inputData.getTa());
        Double humidity = inputData.getHm() != null ? parseDoubleSafe(inputData.getHm()) : null;

        Prediction bestMatch = null;
        double bestScore = Double.MAX_VALUE;

        for (Prediction condition : conditions) {
            double score = calculateSimilarityScore(solarRadiation, temperature, humidity, condition);
            if (score < bestScore) {
                bestScore = score;
                bestMatch = condition;
            }
        }

        return bestMatch;
    }

    // -------------------------------
    // 유사도 점수 계산 (정규화)
    // -------------------------------
    private double calculateSimilarityScore(
            double solarRadiation,
            double temperature,
            Double humidity,
            Prediction condition
    ) {
        double solarDiff = Math.abs(solarRadiation - mid(condition.getCumulativeSolarMin(), condition.getCumulativeSolarMax()))
                / mid(condition.getCumulativeSolarMin(), condition.getCumulativeSolarMax());
        double tempDiff = Math.abs(temperature - mid(condition.getOutsideTempMin(), condition.getOutsideTempMax()))
                / mid(condition.getOutsideTempMin(), condition.getOutsideTempMax());

        double humidityDiff = 0;
        if (humidity != null && condition.getWeeklyAvgHumidityMin() != null && condition.getWeeklyAvgHumidityMax() != null) {
            humidityDiff = Math.abs(humidity - mid(condition.getWeeklyAvgHumidityMin(), condition.getWeeklyAvgHumidityMax()))
                    / mid(condition.getWeeklyAvgHumidityMin(), condition.getWeeklyAvgHumidityMax());
        }

        return (solarDiff * SOLAR_WEIGHT) + (tempDiff * TEMP_WEIGHT) + (humidityDiff * HUMIDITY_WEIGHT);
    }

    // -------------------------------
    // 오차율 반영 yield 계산
    // -------------------------------
    private double applyError(Prediction bestMatch, WeatherDto.WeatherData inputData) {
        double solarRadiation = parseDoubleSafe(inputData.getSi()) * 1000;
        double temperature = parseDoubleSafe(inputData.getTa());
        Double humidity = inputData.getHm() != null ? parseDoubleSafe(inputData.getHm()) : null;

        double errorTa = Math.abs(temperature - mid(bestMatch.getOutsideTempMin(), bestMatch.getOutsideTempMax()))
                / mid(bestMatch.getOutsideTempMin(), bestMatch.getOutsideTempMax());

        double errorHm = 0;
        if (humidity != null && bestMatch.getWeeklyAvgHumidityMin() != null && bestMatch.getWeeklyAvgHumidityMax() != null) {
            errorHm = Math.abs(humidity - mid(bestMatch.getWeeklyAvgHumidityMin(), bestMatch.getWeeklyAvgHumidityMax()))
                    / mid(bestMatch.getWeeklyAvgHumidityMin(), bestMatch.getWeeklyAvgHumidityMax());
        }

        double errorSi = Math.abs(solarRadiation - mid(bestMatch.getCumulativeSolarMin(), bestMatch.getCumulativeSolarMax()))
                / mid(bestMatch.getCumulativeSolarMin(), bestMatch.getCumulativeSolarMax());

        double avgError = (errorTa + errorHm + errorSi) / 3.0;

        return Math.round(bestMatch.getYield() * (1 - avgError) * 10) / 10.0;
    }

    // -------------------------------
    // 유틸
    // -------------------------------
    private double mid(Double min, Double max) {
        return (min + max) / 2.0;
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid numeric value: " + value, e);
        }
    }
}
