package org.example.groworders.domain.farms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.groworders.config.push.event.PushEvent;
import org.example.groworders.domain.farms.model.dto.FarmDto;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.example.groworders.domain.farms.repository.FarmQueryRepository;
import org.example.groworders.domain.farms.repository.FarmRepository;
import org.example.groworders.domain.users.service.S3PresignedUrlService;
import org.springframework.context.ApplicationEventPublisher;
import org.example.groworders.domain.users.service.S3UploadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FarmService {
    private final FarmQueryRepository farmQueryRepository;
    private final FarmRepository farmRepository;
    private final S3UploadService s3UploadService; //업로드
    private final S3PresignedUrlService s3PresignedUrlService; //불러오기
    private final ApplicationEventPublisher publisher;

    // 농장 등록
    @Transactional
    public FarmDto.FarmRegisterResponse register(FarmDto.Register dto,
                                                 MultipartFile farmImageUrl,
                                                 Long userId) throws IOException {
        // 농장 이미지 업로드
        String filePath = s3UploadService.upload(farmImageUrl);
        Farm farm = farmRepository.save(dto.toEntity(userId, filePath));

        // 푸시 테스트용: 농장 등록 시 농부에게 알림 전송
        farmRegisterPush(farm);
//        return FarmDto.FarmResponse.from(farm);

        //소유하고 있는 농장 리스트 응답
        List<Farm> ownedFarm = farmQueryRepository.findByIdWithFarmWithCrop(userId);
        return FarmDto.FarmRegisterResponse.from(ownedFarm);
    }

    // 농장 정보
    @Transactional(readOnly = true)
    public FarmDto.FarmListResponse read(Long id) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("농장을 찾을 수 없습니다."));
        String presignedUrl = farm.getFarmImage() != null ?
                s3PresignedUrlService.generatePresignedUrl(farm.getFarmImage(), Duration.ofMinutes(60)) :
                s3PresignedUrlService.generatePresignedUrl("not-found-image.jpg", Duration.ofMinutes(60));
        return FarmDto.FarmListResponse.from(farm, presignedUrl);
    }

    // 농장 정보 수정
    @Transactional
    public FarmDto.FarmResponse update(Long farmId,
                                       FarmDto.Update dto,
                                       Long id) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new IllegalArgumentException("농장을 찾을 수 없습니다."));

        // 소유자 검증
        if (!farm.getUser().getId().equals(id)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }
        dto.update(farm);
        return FarmDto.FarmResponse.from(farm);
    }

    // 농장 리스트
    public List<FarmDto.FarmListResponse> listAll() {
        List<Farm> farmList = farmRepository.findFarmsHavingAnyCrop();
        return farmList.stream().map(farm -> {
            String presignedUrl = farm.getFarmImage() != null ?
                    s3PresignedUrlService.generatePresignedUrl(farm.getFarmImage(), Duration.ofMinutes(60)) :
                    s3PresignedUrlService.generatePresignedUrl("not-found-image.jpg", Duration.ofMinutes(60));
            return FarmDto.FarmListResponse.from(farm, presignedUrl);
        }).toList();
    }

    // 푸시 테스트용: 농장 등록 알림 발송
    public void farmRegisterPush(Farm farm) {
        publisher.publishEvent(PushEvent.builder()
                .userId(farm.getUser().getId())
                .title("농장이 등록되었습니다.")
                .message(String.format("'%s' 농장이 등록되었습니다.", farm.getName()))
                .icon("iconimage")
                .url("/farms/" + farm.getId())
                .build()
        );
    }
}
