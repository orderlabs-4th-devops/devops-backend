package org.example.groworders.domain.users.service;

import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import org.example.groworders.common.exception.FileUploadException;
import org.example.groworders.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3UploadService implements UploadService {
    // @Value 어노테이션 : yml 파일에 작성한 내용 불러오는 어노테이션
    @Value("${spring.cloud.aws.s3.bucket}")
    private String s3BucketName;
    private final S3Operations s3Operations;

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("프로필 이미지를 선택해주세요.");
        }

        try {
            String dirPath = FileUploadUtil.makeUploadPath();
            S3Resource s3Resource = s3Operations.upload(s3BucketName, dirPath + file.getOriginalFilename(), file.getInputStream());
            return s3Resource.getFilename();
        } catch (IOException e) {
            throw new FileUploadException("프로필 이미지 업로드 실패", e);
        }
    }
}