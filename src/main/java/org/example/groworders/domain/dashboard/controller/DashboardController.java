package org.example.groworders.domain.dashboard.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.example.groworders.common.model.BaseResponse;
import org.example.groworders.dashboard.dto.DashboardDto;
import org.example.groworders.domain.dashboard.model.dto.ChartResponse;
import org.example.groworders.domain.dashboard.model.dto.RecentOrdersResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Hidden
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/recent")
    public ResponseEntity<BaseResponse<RecentOrdersResponse>> getRecentOrders() {
        RecentOrdersResponse response = RecentOrdersResponse.builder()
                .title("최근 주문자 정보")
                .orders(Arrays.asList(
                        RecentOrdersResponse.Order.builder().img("boy.png").name("이시욱").item("토마토").itemsSold(432).orderAmount(230900).build(),
                        RecentOrdersResponse.Order.builder().img("man.png").name("구창모").item("파프리카").itemsSold(231).orderAmount(544130).build(),
                        RecentOrdersResponse.Order.builder().img("woman.png").name("윤소민").item("딸기").itemsSold(562).orderAmount(143960).build(),
                        RecentOrdersResponse.Order.builder().img("girl.png").name("유현경").item("딸기").itemsSold(151).orderAmount(29410).build(),
                        RecentOrdersResponse.Order.builder().img("boy.png").name("이시욱").item("토마토").itemsSold(245).orderAmount(152500).build()
                ))
                .build();

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @GetMapping("/summary")
    public ResponseEntity<BaseResponse<org.example.groworders.dashboard.dto.DashboardDto>> getDashboard() {

        org.example.groworders.dashboard.dto.DashboardDto dashboard = org.example.groworders.dashboard.dto.DashboardDto.builder()
                .summary(Arrays.asList(
                        DashboardDto.Summary.builder()
                                .title("예상 수익금")
                                .value("$53,000")
                                .change(new DashboardDto.Change("55", "up", "since yesterday"))
                                .icon(new DashboardDto.Icon("ni ni-money-coins", "bg-gradient-primary", "rounded-circle", "#ffffff"))
                                .build(),

                        DashboardDto.Summary.builder()
                                .title("예상 생산량")
                                .value("2,300")
                                .change(new DashboardDto.Change("3", "up", "since last week"))
                                .icon(new DashboardDto.Icon("ni ni-world", "bg-gradient-danger", "rounded-circle", "#ffffff"))
                                .build(),

                        DashboardDto.Summary.builder()
                                .title("요청 생산량")
                                .value("+3,462")
                                .change(new DashboardDto.Change("2", "down", "since last quarter"))
                                .icon(new DashboardDto.Icon("ni ni-paper-diploma", "bg-gradient-success", "rounded-circle", "#ffffff"))
                                .build(),

                        DashboardDto.Summary.builder()
                                .title("판매량")
                                .value("$103,430")
                                .change(new DashboardDto.Change("5", "up", "than last month"))
                                .icon(new DashboardDto.Icon("ni ni-cart", "bg-gradient-warning", "rounded-circle", "#ffffff"))
                                .build()
                ))
                .build();

        return ResponseEntity.ok(BaseResponse.success(dashboard));
    }

    @GetMapping("/charts")
    public ResponseEntity<BaseResponse<ChartResponse>> getCharts() {

        ChartResponse response = ChartResponse.builder()
                .charts(Arrays.asList(
                        ChartResponse.ChartItem.builder()
                                .id("tomato")
                                .title("Tomato Sales Overview")
                                .chart(new ChartResponse.Chart(
                                        Arrays.asList("Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"),
                                        Arrays.asList(new ChartResponse.Dataset("Mobile Apps",
                                                Arrays.asList(50, 40, 300, 220, 500, 250, 400, 230, 500)))
                                ))
                                .build(),

                        ChartResponse.ChartItem.builder()
                                .id("paprika")
                                .title("Paprika Sales Overview")
                                .chart(new ChartResponse.Chart(
                                        Arrays.asList("Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"),
                                        Arrays.asList(new ChartResponse.Dataset("Mobile Apps",
                                                Arrays.asList(60, 50, 320, 240, 520, 270, 420, 250, 520)))
                                ))
                                .build(),

                        ChartResponse.ChartItem.builder()
                                .id("strawberry")
                                .title("Strawberry Sales Overview")
                                .chart(new ChartResponse.Chart(
                                        Arrays.asList("Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"),
                                        Arrays.asList(new ChartResponse.Dataset("Mobile Apps",
                                                Arrays.asList(70, 60, 340, 260, 540, 290, 440, 270, 540)))
                                ))
                                .build()
                ))
                .build();

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
