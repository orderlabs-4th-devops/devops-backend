package org.example.groworders.domain.farms.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.groworders.domain.crops.model.dto.CropDto;
import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.example.groworders.domain.users.model.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FarmDto {

    // 농장 등록
    @Getter
    public static class Register {
        @NotBlank(message = "농장 이름를 입력해주세요.")
        @Size(max = 20)
        private String name;

        @NotBlank(message = "농장 지역은 필수 선택입니다.")
        private String region;

        @NotBlank(message = "농장 주소를 입력해주세요.")
        @Size(max = 50)
        private String address;

        @NotNull(message = "농장 면적을 입력해주세요.")
        @Min(value = 10)
        private Integer size;

        @NotBlank(message = "농장 설명을 입력해주세요.")
        @Size(max = 100)
        private String contents;

        private String farmImageUrl; // 농장 프로필 사진은 필수 아님

        public Farm toEntity(Long userId, String farmImageUrl) {
            User user = User.builder()
                    .id(userId)
                    .build();

            return Farm.builder()
                    .name(name)
                    .region(region)
                    .address(address)
                    .size(size)
                    .contents(contents)
                    .farmImage(farmImageUrl)
                    .user(user)
                    .build();
        }
    }

    // 농장 정보
    @Getter
    public static class Read{
        private String name;
        private String region;
        private String address;
        private Integer size;
        private String contents;
        private String farmImage;

        public static Farm from(Farm entity) {
            return Farm.builder()
                    .name(entity.getName())
                    .region(entity.getRegion())
                    .address(entity.getAddress())
                    .size(entity.getSize())
                    .contents(entity.getContents())
                    .farmImage(entity.getFarmImage())
                    .cropList(entity.getCropList())
                    .build();
        }
    }

    // 농장 정보 수정
    @Getter
    public static class Update {
        @NotBlank(message = "농장 이름를 입력해주세요.")
        @Size(max = 20)
        private String name;

        @NotBlank(message = "농장 지역은 필수 선택입니다.")
        private String region;

        @NotBlank(message = "농장 주소를 입력해주세요.")
        @Size(max = 50)
        private String address;

        @NotNull(message = "농장 면적을 입력해주세요.")
        @Min(value = 10)
        private Integer size;

        @NotBlank(message = "농장 설명을 입력해주세요.")
        @Size(max = 100)
        private String contents;

        private String farmImage;

        public void update(Farm entity) {
            entity.setName(name);
            entity.setRegion(region);
            entity.setAddress(address);
            entity.setSize(size);
            entity.setContents(contents);
            entity.setFarmImage(farmImage);
        }
    }

/*
    // 농장 리스트
    @Builder
    @Getter
    public static class FarmList {
        private Boolean isSuccess;
        private List<FarmListResponse> farmResult;
        public static FarmList from(List<Farm> entityList) {
            return FarmList.builder()
                    .isSuccess(true)
                    .farmResult(entityList.stream().map(entity -> FarmListResponse.from(entity)).toList())
                    .build();
        }
    }
*/


    /*// FarmListResponse (작물 포함)
    @Getter
    @Builder
    public static class FarmListResponse {
        private Long user_id;
        private Long id;
        private String name;
        private String region;
        private String address;
        private Integer size;
        private String contents;
        private String farmImage;
        private List<Crop> cropList;
        private List<String> cropType;
        private List<String> cropCultivateType;
        private List<Integer> cropExpectedQuantity;
        // private Integer cropPrice;

        public static FarmListResponse from(Farm entity) {
            return FarmListResponse.builder()
                    .user_id(entity.getUser().getId())
                    .id(entity.getId())
                    .name(entity.getName())
                    .region(entity.getRegion())
                    .address(entity.getAddress())
                    .size(entity.getSize())
                    .contents(entity.getContents())
                    .farmImage(entity.getFarmImage())
                    .cropType(entity.getCropList().stream().map(Crop::getType).toList())
                    .cropCultivateType(entity.getCropList().stream().map(Crop::getCultivateType).toList())
                    .cropExpectedQuantity(entity.getCropList().stream().map(Crop::getExpectedQuantity).toList())
                    .build();
        }
    }*/

    // FarmListResponse (작물 포함)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FarmListResponse {
        private Long user_id;
        private Long id;
        private String name;
        private String region;
        private String address;
        private Integer size;
        private String contents;
        private String farmImage; // presigned URL
        private List<CropDto.CropResponse> cropList;
//        private List<String> cropType;
//        private List<String> cropCultivateType;
//        private List<Integer> cropExpectedQuantity;
        // private Integer cropPrice;

        public static FarmListResponse from(Farm entity, String presignedUrl) {
            List<Crop> crops = Optional.ofNullable(entity.getCropList()).orElseGet(List::of);

            return FarmListResponse.builder()
                    .user_id(entity.getUser().getId())
                    .id(entity.getId())
                    .name(entity.getName())
                    .region(entity.getRegion())
                    .address(entity.getAddress())
                    .size(entity.getSize())
                    .contents(entity.getContents())
                    .farmImage(presignedUrl)
                    .cropList(crops.stream().map(CropDto.CropResponse::from).toList())
//                    .cropType(crops.stream().map(Crop::getType).toList())
//                    .cropCultivateType(crops.stream().map(Crop::getCultivateType).toList())
//                    .cropExpectedQuantity(crops.stream().map(Crop::getExpectedQuantity).toList())
                    .build();
        }
    }

    // FarmResponse (작물 미포함)
    @Getter
    @Builder
    public static class FarmResponse {
        private Long user_id;
        private Long id;
        private String name;
        private String region;
        private String address;
        private Integer size;
        private String contents;
        private String farmImage;

        public static FarmResponse from(Farm entity) {
            return FarmResponse.builder()
                    .user_id(entity.getUser().getId())
                    .id(entity.getId())
                    .name(entity.getName())
                    .region(entity.getRegion())
                    .address(entity.getAddress())
                    .size(entity.getSize())
                    .contents(entity.getContents())
                    .farmImage(entity.getFarmImage())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FarmRegisterResponse {
        private List<FarmDto.OwnedFarm> ownedFarm;

        public static  FarmRegisterResponse from(List<Farm> entity) {
            return FarmRegisterResponse.builder()
                    .ownedFarm(entity.stream().map(FarmDto.OwnedFarm::from).toList())
                    .build();
        }
    }


/*
    // FarmResponse
    @Getter
    @Builder
    public static class FarmResponse {
        private Long user_id;
        private Long id;
        private String name;
        private String region;
        private String address;
        private Integer size;
        private String contents;
        private String farmImage;
        private List<CropDto.CropResponse> cropList;

        public static FarmResponse from(Farm entity, String presignedUrl) {
            return FarmResponse.builder()
                    .user_id(entity.getUser().getId())
                    .id(entity.getId())
                    .name(entity.getName())
                    .region(entity.getRegion())
                    .address(entity.getAddress())
                    .size(entity.getSize())
                    .contents(entity.getContents())
                    .farmImage(presignedUrl)
                    .cropList(entity.getCropList().stream().map(CropDto.CropResponse::from).toList())
                    .build();
        }
    }
*/

    //로그인 시 농장 정보 응답할 데이터
    @Getter
    @Builder
    public static class OwnedFarm {
        private Long id;
        private String name;
        private List<String> cropType;

        public static OwnedFarm from(Farm entity) {
            return OwnedFarm.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .cropType(Optional.ofNullable(entity.getCropList())
                            .orElseGet(Collections::emptyList)
                            .stream()
                            .map(Crop::getType)
                            .distinct()
                            .toList()) //Crop Entity에서 Type 중복 제거하고 가져오기
                    .build();
        }
    }


}