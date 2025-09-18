package org.example.groworders.domain.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.groworders.common.model.BaseResponse;
import org.example.groworders.domain.users.model.dto.UserDto;
import org.example.groworders.domain.users.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "회원 기능")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "회원가입 기능",
            description = "회원 가입 기능"
    )
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<Void>> signup(
            @RequestPart(value = "signupInfo") @Valid UserDto.SignUp dto,
            @RequestPart(value = "profileImageUrl", required = false) MultipartFile profileImageUrl
    ) throws MessagingException, IOException, SQLException {
        userService.signup(dto, profileImageUrl);
        return ResponseEntity.ok(BaseResponse.successMessage("등록 성공"));
    }

    @Operation(
            summary = "E-mail 검증 API",
            description = "E-mail 검증 API"
    )
    @GetMapping("/verify")
    public ResponseEntity<BaseResponse<Void>> verify(@RequestParam String uuid) {
        userService.verify(uuid);
        return ResponseEntity.ok(BaseResponse.successMessage("등록 성공"));
    }
}
