package org.example.groworders.domain.crops.model.entity;

import lombok.*;

@Getter
public enum CropStatus {

    BEST("양호"),
    NORMAL("보통"),
    BAD("불량");

    private final String status;

    CropStatus(String status) {
        this.status = status;
    }
}
