package org.example.groworders.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DashboardDto {
    private List<Summary> summary;

    @Data
    @Builder
    @AllArgsConstructor
    public static class Summary {
        private String title;
        private String value;
        private Change change;
        private Icon icon;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Change {
        private String percentage;
        private String trend;
        private String text;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Icon {
        private String component;
        private String background;
        private String shape;
        private String color;
    }
}
