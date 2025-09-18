package org.example.groworders.domain.crops.service;

import lombok.RequiredArgsConstructor;
import org.example.groworders.common.exception.BaseException;
import org.example.groworders.domain.crops.model.dto.CropDto;
import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.crops.repository.CropRepository;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.example.groworders.domain.farms.repository.FarmQueryRepository;
import org.example.groworders.domain.farms.repository.FarmRepository;
import org.example.groworders.domain.inventories.model.dto.InventoryDto;
import org.example.groworders.domain.predict.model.dto.PredictionDto;
import org.springframework.stereotype.Service;

import static org.example.groworders.common.model.BaseResponseStatus.INVALID_FARM_INFO;

@Service
@RequiredArgsConstructor
public class CropService {
    private final CropRepository cropRepository;
    private final FarmRepository farmRepository;

    //작물 등록
    public Crop register(CropDto.Register dto) {
        Farm farm = farmRepository.findById(dto.getFarmId()).orElseThrow(() -> BaseException.from(INVALID_FARM_INFO));

        //농장이 존재하면 작물 등록
        return cropRepository.save(dto.toEntity());
    }


    //예측 생산량을 요청할 DTO 반환
    public PredictionDto.RequestDaily transform(Crop crop) {

        String month = String.format("%02d", crop.getSowingStartDate().getMonthValue()); //'월'만 추출

        return PredictionDto.RequestDaily.builder()
                .cropName(crop.getType())
                .cultivationType(crop.getCultivateType())
                .growthStage(month)
                .build();
    }


    //예측 생산량을 재고로 등록할 DTO로 가공
    public InventoryDto.Register transform(Crop crop, PredictionDto.Response prediction) {

        Integer expectedQuantity = prediction.getPredictedYield() != null ?(int) Math.floor(Double.parseDouble(prediction.getPredictedYield())) : 0; //예측 수확량

        return InventoryDto.Register.builder()
                .expectedHarvestDate(crop.getSowingStartDate().plusMonths(6)) //파종 시작일 + 6개월
                .expectedQuantity(expectedQuantity * crop.getArea())
                .maxExpectedQuantity(expectedQuantity * crop.getArea())
                .cropId(crop.getId())
                .build();
    }
}
