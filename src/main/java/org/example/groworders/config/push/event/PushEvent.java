package org.example.groworders.config.push.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
    public class PushEvent {
        private final Long userId;
        private final String title;
        private final String message;
        private final String icon;
        private final String url;
    }
