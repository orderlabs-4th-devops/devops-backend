package org.example.groworders.domain.farms.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {
    Optional<Farm> findById(@NotNull(message = "농장은 필수 선택입니다.") @Positive(message = "농장을 확인 해주세요.") Long farmId);

    @Query("""
    select f from Farm f
    where exists (select 1 from Crop c where c.farm = f)
    """)
    List<Farm> findFarmsHavingAnyCrop();
}

