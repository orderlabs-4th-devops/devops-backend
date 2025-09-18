package org.example.groworders.domain.dashboard.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ChartResponse {
    private List<ChartItem> charts;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ChartItem {
        private String id;
        private String title;
        private Chart chart;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Chart {
        private List<String> labels;
        private List<Dataset> datasets;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Dataset {
        private String label;
        private List<Integer> data;
    }
}
