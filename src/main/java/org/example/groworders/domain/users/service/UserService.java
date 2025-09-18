package org.example.groworders.domain.users.service;

import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.groworders.common.exception.*;
import org.example.groworders.domain.farms.model.dto.FarmDto;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.example.groworders.domain.farms.repository.FarmQueryRepository;
import org.example.groworders.domain.users.model.dto.EmailVerify;
import org.example.groworders.domain.users.model.dto.UserDto;
import org.example.groworders.domain.users.model.entity.User;
import org.example.groworders.domain.users.repository.EmailVerifyRepository;
import org.example.groworders.domain.users.repository.UserRepository;
import org.example.groworders.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final FarmQueryRepository farmQueryRepository;
    private final JavaMailSender emailSender;
    private final EmailVerifyRepository emailVerifyRepository;
    private final S3UploadService s3UploadService;
    private final S3PresignedUrlService s3PresignedUrlService;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String s3BucketName;

    @Transactional
    public void signup(UserDto.SignUp dto, MultipartFile profileImageUrl) {
        // 프로필 업로드
        String filePath = s3UploadService.upload(profileImageUrl);

        // 회원 저장
        User user = dto.toEntity(filePath);
        user.passwordEncrypted(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // 3. 이메일 인증
        sendVerificationEmail(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        // Security용 UserDetails 반환
        return userRepository.findByEmail(email)
                .map(UserDto.AuthUser::from)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public UserDto.AuthUser loadAuthUserWithPresignedUrl(String email) {
        // 클라이언트용 AuthUser DTO
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        String presignedUrl = user.getProfileImage() != null
                ? s3PresignedUrlService.generatePresignedUrl(user.getProfileImage(), Duration.ofMinutes(60))
                : null;

        List<Farm> ownedFarm = farmQueryRepository.findByIdWithFarmWithCrop(user.getId());
        return UserDto.AuthUser.from(user, presignedUrl, ownedFarm);
    }

    @Transactional
    public void verify(String uuid) {
        EmailVerify emailVerify = emailVerifyRepository.findByUuid(uuid)
                .orElseThrow(() -> new EmailVerifyException("이메일 인증에 실패했습니다."));

        User user = emailVerify.getUser();
        user.userVerify();
        userRepository.save(user);
    }

    private void sendVerificationEmail(User user) {
        String uuid = UUID.randomUUID().toString();
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("[내 사이트] 가입 환영");
            String htmlContent = "<h2>가입을 환영합니다!</h2>"
                    + "<a href='http://localhost:8080/users/verify?uuid=" + uuid + "'>이메일 인증하기</a>";
            helper.setText(htmlContent, true);

            emailSender.send(message);

        } catch (MessagingException e) {
            throw new EmailSendException("가입 이메일 전송 실패", e);
        }

        EmailVerify emailVerify = EmailVerify.builder()
                .uuid(uuid)
                .user(user)
                .build();
        emailVerifyRepository.save(emailVerify);
    }
}
