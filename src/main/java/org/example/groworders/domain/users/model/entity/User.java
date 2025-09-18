package org.example.groworders.domain.users.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.groworders.config.push.model.entity.PushHistory;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.example.groworders.domain.users.model.dto.EmailVerify;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다")
    private String password;

    @NotBlank(message = "계정 아이디는 필수입니다")
    @Size(max = 20, message = "계정 아이디는 최대 20자리까지 가능합니다")
    private String accountId;

    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 30)
    private String name;

    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "전화번호 형식이 올바르지 않습니다")
    private String phoneNumber;

    @Past(message = "생년월일은 과거 날짜여야 합니다")
    private LocalDate birthDate;

    @Setter
    private String profileImage;

    private Boolean enabled;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'USER'")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailVerify> emailVerifyList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    List<Farm> farmList;

    @OneToMany(mappedBy = "user")
    List<PushHistory> pushHistoryList;

    public void userVerify() {
        this.enabled = true;
    }

    public void passwordEncrypted(String encodedPassword) {
        this.password = encodedPassword;
    }
}