package org.example.groworders.domain.crops.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.example.groworders.domain.inventories.model.dto.InventoryDto;
import org.hibernate.annotations.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //작물 종류(이름) : not null, 길이 50
    @Column(nullable = false, length = 50)
    private String type;

    //작물 상태 : 길이 2, 기본값 '양호'
    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    @ColumnDefault("'BEST'")
    private CropStatus status;

    //단가 : not null
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer price;

    //파종 시작일 : not null, 기본값 현재 시간
    @Column(nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDate sowingStartDate;

    //재배 면적 : not null, 기본값 0, 0 혹은 양수
    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer area;

    //재배 방식 : not null, 길이 2
    @Column(nullable = false, length = 2)
    private String cultivateType;

    //판매 상태
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NOT_AVAILABLE'")
    private SaleStatus saleStatus;

    private LocalDate expectedHarvestDate; //예측 수확일
    private Integer expectedQuantity; //예측 수확량
    private Integer maxExpectedQuantity; //최대 수확량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id")
    private Farm farm; //작물을 소유한 농장 : 외래키

    public void registerInventory(InventoryDto.Register dto) {
        expectedHarvestDate = dto.getExpectedHarvestDate();
        expectedQuantity = dto.getExpectedQuantity();
        maxExpectedQuantity = dto.getMaxExpectedQuantity();

        if(dto.getExpectedQuantity() != 0) {
            saleStatus = SaleStatus.AVAILABLE;
        }
    }

    public void updateInventory(InventoryDto.Update dto) {
        area = dto.getArea();
        expectedHarvestDate = dto.getExpectedHarvestDate();
        expectedQuantity = dto.getExpectedQuantity();
        maxExpectedQuantity = dto.getMaxExpectedQuantity();
        sowingStartDate = dto.getSowingStartDate();
    }
}
