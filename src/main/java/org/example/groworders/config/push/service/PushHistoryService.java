package org.example.groworders.config.push.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.groworders.config.push.event.PushEvent;
import org.example.groworders.config.push.model.dto.PushDto;
import org.example.groworders.config.push.model.entity.PushHistory;
import org.example.groworders.config.push.model.entity.PushType;
import org.example.groworders.config.push.repository.PushHistoryRepository;
import org.example.groworders.domain.users.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushHistoryService {
    private final PushHistoryRepository pushHistoryRepository;
    private final EntityManager mapper;

    // 푸시 알림 발송 시 히스토리 작성
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long write(PushEvent event) {
        User userRef = mapper.getReference(User.class, event.getUserId());

        PushHistory h = PushHistory.builder()
                .user(userRef)
                .type(PushType.ORDER_REGISTER_NOTIFICATION)
                .title(event.getTitle())
                .message(event.getMessage())
                .read(false)
                .build();

        return pushHistoryRepository.saveAndFlush(h).getId();
    }

    // 푸시 알림 히스토리 출력
    public List<PushDto.PushResponse> history(Long id) {
        List<PushHistory> pushHistoryList = pushHistoryRepository.findByUserIdOrderByCreatedAtDesc(id);
        return pushHistoryList.stream().map(PushDto.PushResponse::from).toList();
    }

}