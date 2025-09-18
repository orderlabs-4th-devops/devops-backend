package org.example.groworders.domain.users.model.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import org.example.groworders.domain.farms.model.dto.FarmDto;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.example.groworders.domain.users.model.entity.Role;
import org.example.groworders.domain.users.model.entity.User;
import org.example.groworders.domain.users.service.S3PresignedUrlService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
 * @Pattern : 정규표현식을 이용해서 원하는 값만 허용
 * @Null : null만 허용
 * @NotNull : 빈 문자열 "" , 공백은 허용 " ", null은 안됨
 * @NotEmpty : " " 허용, null 밑 ""은 안됨
 * @NotBlank : null, "", " " 다 안됨
 * @Size(min=5, max=10) : 길이를 검증할 때 사용
 * @Max : 최대만 지정
 * @Min : 최소만 지정
 * @Positive : 양수만 가능
 * @PositiveOfZero : 양수 및 0까지 가능
 * @Negative : 음수만 가능
 * @NegativeOfZero : 음수 및 0까지 가능
 * @Future : 현재 시간보다 미래만 허용
 * @FutureOrPresent : 현재 시간과 미래만 허용
 * @Past : 현재 시간보다 과거만 허용
 * @PastOrPresent : 현재 시간과 과거만 허용
 */

public class UserDto {
    @Getter
    public static class SignIn {
        private String email;
        private String password;
        private Role role;
    }

    @Getter
    @Builder
    public static class SignInResponse {
        private Long id;
        private String email;
        private String name;
        private Role role;
        private String profileImage;
        private List<FarmDto.OwnedFarm> ownedFarm;

        public static SignInResponse from(AuthUser authUser) {

            return SignInResponse.builder()
                    .id(authUser.getId())
                    .email(authUser.getEmail())
                    .name(authUser.getName())
                    .role(authUser.getRole())
                    .profileImage(authUser.getProfileImage())
                    .ownedFarm(authUser.getOwnedFarm())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class UserResponse {
        private Long id;
        private String email;
        private String name;
        private Role role;

        public static UserResponse from(User entity) {

            return UserResponse.builder()
                    .id(entity.getId())
                    .email(entity.getEmail())
                    .name(entity.getName())
                    .role(entity.getRole())
                    .build();
        }
    }

    @Getter
    public static class SignUp {
        @NotBlank(message = "계정 아이디는 필수입니다")
        @Size(max = 20, message = "계정 아이디는 최대 20자리까지 가능합니다")
        private String accountId;

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "이메일 형식을 사용해 주세요")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다")
        private String password;

        @NotBlank(message = "이름은 필수입니다")
        @Size(max = 30, message = "이름은 최대 30자리까지 가능합니다")
        private String name;

        @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "전화번호 형식이 올바르지 않습니다")
        private String phoneNumber;

        @Past(message = "생년월일은 과거 날짜여야 합니다")
        private LocalDate birthDate;

        private Role role;

        public User toEntity(String profileImageUrl) {
            return User.builder()
                    .accountId(accountId)
                    .email(email)
                    .password(password)
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .birthDate(birthDate)
                    .role(role != null ? role : Role.USER)
                    .profileImage(profileImageUrl)
                    .enabled(false)
                    .build();
        }
    }


    @Getter
    @Builder
    public static class AuthUser implements UserDetails, OAuth2User {
        private Long id;
        private String email;
        private String password;
        private String name;
        private Role role;
        private Boolean enabled;
        private String profileImage;
        private Map<String, Object> attributes;
        private List<FarmDto.OwnedFarm> ownedFarm;

        @Override
        public Map<String, Object> getAttributes() {
            return attributes;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        // 기존 from(User) 유지 (Security용)
        public static AuthUser from(User entity) {
            return AuthUser.builder()
                    .id(entity.getId())
                    .email(entity.getEmail())
                    .password(entity.getPassword())
                    .name(entity.getName())
                    .role(entity.getRole() != null ? entity.getRole() : Role.USER)
                    .enabled(entity.getEnabled())
                    .profileImage(entity.getProfileImage()) // DB에 저장된 URL
                    .build();
        }

        // 클라이언트용 Presigned URL 포함
        public static AuthUser from(User userEntity, String presignedUrl, List<Farm> farmEntity) {
            return AuthUser.builder()
                    .id(userEntity.getId())
                    .email(userEntity.getEmail())
                    .password(userEntity.getPassword())
                    .name(userEntity.getName())
                    .role(userEntity.getRole() != null ? userEntity.getRole() : Role.USER)
                    .enabled(userEntity.getEnabled())
                    .profileImage(presignedUrl) // Presigned URL
                    .ownedFarm(farmEntity.stream().map(FarmDto.OwnedFarm::from).toList())
                    .build();
        }


        public User toEntity() {
            return User.builder()
                    .id(id)
                    .build();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        @Override
        public String getUsername() {
            return email;
        }

    }

}
