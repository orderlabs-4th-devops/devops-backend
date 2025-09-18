package org.example.groworders.config.push.repository;

import org.example.groworders.config.push.model.entity.PushHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PushHistoryRepository extends JpaRepository<PushHistory, Long> {
    List<PushHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}