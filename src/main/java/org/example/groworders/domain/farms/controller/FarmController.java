package org.example.groworders.domain.farms.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.groworders.common.model.BaseResponse;
import org.example.groworders.domain.farms.model.dto.FarmDto;
import org.example.groworders.domain.farms.service.FarmService;
import org.example.groworders.domain.users.model.dto.UserDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;


//User
//@OneToMany(mappedBy = "user")
//List<Farm> farmList;

//UserRepository
//User getReferenceById(Long userId);

// Crop
//@ManyToOne
//@JoinColumn(name = "crop")
//private Farm farm;

//@ManyToOne
//@JoinColumn(name = "crop")
//private Crop crop;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farms")
public class FarmController {
    private final FarmService farmservice;

    // 농장 등록
    @Operation(
            summary = "농장 등록",
            description = "농장 이름, 농장 지역, 농장 주소, 농장 크기, 농장 설명, 농장 프로필 사진을 입력받아 농장을 등록한다."
    )
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<FarmDto.FarmRegisterResponse>> register(
            @RequestPart("dto") @Valid FarmDto.Register dto,
            @RequestPart(value = "farmImageUrl", required = false) MultipartFile farmImageUrl,
            @AuthenticationPrincipal UserDto.AuthUser authUser) throws IOException {
        FarmDto.FarmRegisterResponse result = farmservice.register(dto, farmImageUrl, authUser.getId());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 농장 상세 조회
    @Operation(
            summary = "농장 정보",
            description = "농장 정보를 조회한다."
    )
    @GetMapping(value="/{farmId}")
    public ResponseEntity<BaseResponse<FarmDto.FarmListResponse>>  detail(
            @PathVariable Long farmId) {
        FarmDto.FarmListResponse result = farmservice.read(farmId);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 농장 정보 수정
    @Operation(
            summary = "농장 정보 수정",
            description = "농장 정보를 수정한다."
    )
    @PostMapping(value="/{farmId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<FarmDto.FarmResponse>> update(
            @PathVariable Long farmId,
            @RequestPart("dto") @Valid FarmDto.Update dto,
            @RequestPart(value="farmImageUrl", required=false) MultipartFile farmImageUrl,
            @AuthenticationPrincipal UserDto.AuthUser authUser
    ) {
        FarmDto.FarmResponse result = farmservice.update(farmId, dto, authUser.getId());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 농장 리스트 출력
    @Operation(
            summary = "농장 목록",
            description = "농장 목록을 출력한다."
    )
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<List<FarmDto.FarmListResponse>>> list() {
        List<FarmDto.FarmListResponse> result = farmservice.listAll();
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
