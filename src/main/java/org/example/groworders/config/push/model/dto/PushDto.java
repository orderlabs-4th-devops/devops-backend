package org.example.groworders.config.push.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.Subscription;
import org.example.groworders.config.push.model.entity.PushSub;
import org.example.groworders.config.push.model.entity.PushHistory;
import org.example.groworders.config.push.model.entity.PushType;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.List;

public class PushDto {

    // 구독 정보 저장
    @Getter
    @Builder
    public static class Subscribe {
        @Schema(description = "", example = "")
        private String endpoint;
        @Schema(description = "농장이름", example = "김가네 농장")
        private Keys keys;

        // 키 병합
        @Getter
        public static class Keys {
            private String p256dh;
            private String auth;
        }

        public PushSub toEntity(Long userId) {
            return PushSub.builder()
                    .endpoint(endpoint)
                    .p256dh(keys.getP256dh())
                    .auth(keys.getAuth())
                    .userId(userId)
                    .build();
        }
    }

    // 푸시 알림 발송
    @Getter
    @Builder
    public static class Send {
        private String endpoint;
        private Subscription.Keys keys;
        private String payload;
        private Long userId;

        public Notification toEntity() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
            return Notification.builder()
                    .endpoint(endpoint)
                    .userPublicKey(keys.p256dh)
                    .userAuth(keys.auth)
                    .payload(payload)
                    .build();
        }
    }

    // 히스토리 목록
    @Getter
    @Builder
    public static class History {
        private Boolean isSuccess;
        private List<PushResponse> pushResult;
        public static History from(List<PushHistory> entityList) {
            return History.builder()
                    .isSuccess(true)
                    .pushResult(entityList.stream().map(entity -> PushResponse.from(entity)).toList())
                    .build();
        }
    }

    // PushResponse
    @Getter
    @Builder
    public static class PushResponse {
        private Long id;
        private String title;
        private String message;
        private PushType pushType;
        private Boolean isRead;
        private LocalDateTime createdAt;

        public static PushResponse from(PushHistory entity) {
            return PushResponse.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .message(entity.getMessage())
                    .pushType(entity.getType())
                    .isRead(entity.getRead())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }
}
