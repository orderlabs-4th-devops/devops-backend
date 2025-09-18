package org.example.groworders.domain.farms.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.example.groworders.domain.crops.model.entity.QCrop;
import org.example.groworders.domain.crops.model.entity.SaleStatus;
import org.example.groworders.domain.farms.model.entity.Farm;
import org.example.groworders.domain.farms.model.entity.QFarm;
import org.example.groworders.domain.users.model.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FarmQueryRepository {
    private final JPAQueryFactory queryFactory;
    private QUser user;
    private QFarm farm;
    private QCrop crop;

    public FarmQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.user = QUser.user;
        this.farm = QFarm.farm;
        this.crop = QCrop.crop;
    }

    public List<Farm> findByIdWithFarmWithCrop(Long userId) {
        /**
         * select distinct farm.id, farm.name, crop.type from farm
         * left join user on user.id = farm.user_id
         * left join crop on farm.id = crop.farm_id
         * where farm.user_id=1 and (crop.id is null or crop.sale_status not in ('SOLD_OUT', 'DISCONTINUED'));
         * */
        return queryFactory
                .selectFrom(farm)
                .leftJoin(farm.user, user) //실제 엔티티, 사용할 이름
                .leftJoin(farm.cropList, crop).fetchJoin()
                .where(
                        user.id.eq(userId) //user.id = :usesrId
                                .and(crop.isNull().or(crop.saleStatus.notIn(SaleStatus.SOLD_OUT, SaleStatus.DISCONTINUED)))
                )
                .distinct()
                .fetch();
    }
}
