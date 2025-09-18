package org.example.groworders.domain.weather.repository;

import org.example.groworders.domain.weather.model.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface WeatherRepository extends JpaRepository<Weather, Long> {

    @Query("SELECT w.predictYield FROM Weather w WHERE w.tm = :tm")
    Double findYieldByTm(@Param("tm") String tm);

    @Query("SELECT w FROM Weather w WHERE YEAR(w.sowingDate) = :year")
    List<Weather> findBySowingYear(@Param("year") int year);
}

