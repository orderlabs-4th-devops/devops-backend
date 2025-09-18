package org.example.groworders.config.push.repository;

import org.example.groworders.config.push.model.entity.PushSub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PushSubRepository extends JpaRepository<PushSub, Long>{
    boolean existsByEndpoint(String endpoint);
    PushSub findByUserId(Long userId);
}
