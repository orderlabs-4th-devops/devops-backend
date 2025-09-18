package org.example.groworders.domain.crops.repository;

import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {

    /**
     * SELECT * FROM farm
     * LEFT JOIN crop ON farm.id = crop.farm_id
     * WHERE farm.id = [farmId];
     * */
    @Query("SELECT f FROM Farm f LEFT JOIN FETCH f.cropList WHERE f.id = :farmId")
    Optional<Farm> findByIdWithCrop(Long farmId);
}
