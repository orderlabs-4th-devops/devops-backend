package org.example.groworders.domain.crops.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.crops.model.entity.CropStatus;
import org.example.groworders.domain.crops.model.entity.SaleStatus;
import org.example.groworders.domain.farms.model.entity.Farm;

import java.time.LocalDate;
import java.util.Random;

public class CropDto {

    //작물 등록 요청 데이터
    @Getter
    public static class Register {
        @NotNull(message = "작물 종류는 필수 선택입니다.")
        @Pattern(message = "작물 종류를 확인 해주세요.", regexp = "^(?:토마토|딸기|파프리카)$")
        private String type;

        @NotNull(message = "작물 상태의 값을 올바르게 선택 해주세요.")
        private CropStatus status;

        @PositiveOrZero //0이상 숫자
        private Integer price;

        @NotNull(message = "파종 시작일을 선택 해주세요.") //날짜 형식, 윤년까지 체크
        private LocalDate sowingStartDate;

        @NotNull(message="재배 면적을 입력 해주세요.")
        @PositiveOrZero(message = "면적은 0 이상 입력해주세요.") //0이상 숫자
        private Integer area;

        @NotNull(message="재배 방식을 선택 해주세요.")
        @Pattern(message = "재배 방식을 확인 해주세요.", regexp = "^(?:비닐|유리)$")
        private String cultivateType;

        @NotNull(message = "농장은 필수 선택입니다.")
        @Positive(message = "농장을 확인 해주세요.") //1이상 숫자
        private Long farmId;

        public Crop toEntity() {
            //랜덤 단가 생성
            Random random = new Random();
            Integer min = 1000;
            Integer max = 10000;
            Integer value = random.nextInt(max - min + 1) + min;
            Integer randomPrice = (int) (Math.round(value / 10.0) * 10); //일의 자리에서 반올림

            Farm farm = Farm.builder()
                    .id(farmId)
                    .build();

            return Crop.builder()
                    .type(type)
                    .status(status)
                    .price(randomPrice)
                    .sowingStartDate(sowingStartDate)
                    .area(area)
                    .cultivateType(cultivateType)
                    .saleStatus(SaleStatus.NOT_AVAILABLE)
                    .farm(farm)
                    .build();
        }
    }

    //검색 요청 데이터
    @Getter
    @Setter
    public static class Search {
        private String type;
        private CropStatus status;
        private SaleStatus saleStatus;
        private String order;
    }

    //응답 데이터
    @Getter
    @Builder
    public static class CropResponse {
        private Long id;
        private String type; //작물 종류(이름)
        private String status; //작물 상태
        private Integer price; //단가
        private LocalDate sowingStartDate; //파종 시작일
        private Integer area; //재배 면적
        private String cultivateType; //재배 방식
        private SaleStatus saleStatus; //판매 상태
        private Integer orderQuantity; //주문 요청량
        private LocalDate expectedHarvestDate; //예측 수확일
        private Integer expectedQuantity; //예측 수확량
        private Integer maxExpectedQuantity; //최대 수확량

        public static CropResponse from(Crop entity) {
            return CropResponse.builder()
                    .id(entity.getId())
                    .type(entity.getType())
                    .status(entity.getStatus().getStatus())
                    .price(entity.getPrice())
                    .sowingStartDate(entity.getSowingStartDate())
                    .area(entity.getArea())
                    .cultivateType(entity.getCultivateType())
                    .saleStatus(entity.getSaleStatus())
                    .expectedHarvestDate(entity.getExpectedHarvestDate())
                    .expectedQuantity(entity.getExpectedQuantity())
                    .maxExpectedQuantity(entity.getMaxExpectedQuantity())
                    .build();
        }
    }
}
