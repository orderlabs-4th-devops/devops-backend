package org.example.groworders.domain.users.repository;

import org.example.groworders.domain.users.model.dto.EmailVerify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerifyRepository extends JpaRepository<EmailVerify, Long> {
    Optional<EmailVerify> findByUuid(String uuid);
}
