package org.example.groworders.domain.farms.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.users.model.entity.User;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Farm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private String contents;

    private String farmImage;

    // 다대일 (farm:user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 일대다 (farm:crop)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "farm")
    private List<Crop> cropList;
}
