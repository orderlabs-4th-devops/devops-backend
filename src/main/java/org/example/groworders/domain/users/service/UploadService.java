package org.example.groworders.domain.users.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    public String upload(MultipartFile file) throws IOException;
}
