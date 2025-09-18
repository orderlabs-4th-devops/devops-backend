package org.example.groworders.domain.predict.repository;

import org.example.groworders.domain.predict.model.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    @Query("SELECT c FROM Prediction c " +
            "WHERE c.cropName = :cropName " +
            "AND c.cultivationType = :cultivationType " +
            "AND c.growthStage = :growthStage")
    List<Prediction> findByCropAndTypeAndStage(
            @Param("cropName") String cropName,
            @Param("cultivationType") String cultivationType,
            @Param("growthStage") String growthStage);
}