package org.example.groworders.domain.dashboard.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RecentOrdersResponse {
    private String title;
    private List<Order> orders;

    @Data
    @Builder
    @AllArgsConstructor
    public static class Order {
        private String img;
        private String name;
        private String item;
        private Integer itemsSold;
        private Integer orderAmount;
    }
}
