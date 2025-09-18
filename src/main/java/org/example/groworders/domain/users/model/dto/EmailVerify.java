package org.example.groworders.domain.users.model.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.groworders.domain.users.model.entity.User;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class EmailVerify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
