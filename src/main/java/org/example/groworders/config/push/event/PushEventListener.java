package org.example.groworders.config.push.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Subscription;
import org.example.groworders.config.push.model.dto.PushDto;
import org.example.groworders.config.push.model.entity.PushSub;
import org.example.groworders.config.push.repository.PushSubRepository;
import org.example.groworders.config.push.service.PushHistoryService;
import org.example.groworders.config.push.service.PushService;
import org.jose4j.lang.JoseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushEventListener {
    private final PushService pushService;
    private final PushSubRepository pushSubRepository;
    private final PushHistoryService pushHistoryService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPush(PushEvent event) throws JoseException, GeneralSecurityException, IOException, ExecutionException, InterruptedException {
        PushSub sub = pushSubRepository.findByUserId(event.getUserId());
        String payload = buildPayload(event);
        var keys = new Subscription.Keys(sub.getP256dh(), sub.getAuth());
        var dto = PushDto.Send.builder()
                .endpoint(sub.getEndpoint())
                .keys(keys)
                .payload(payload)
                .userId(sub.getUserId())
                .build();

        pushService.send(dto);
        pushHistoryService.write(event);
    }

    private String buildPayload(PushEvent event) {
        var node = mapper.createObjectNode();
        node.put("title", event.getTitle());
        node.put("message", event.getMessage());
        node.put("icon", event.getIcon());
        node.put("url", event.getUrl());
        return node.toString();
    }
}
