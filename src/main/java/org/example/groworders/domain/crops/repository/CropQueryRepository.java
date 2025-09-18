package org.example.groworders.domain.crops.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.example.groworders.domain.crops.model.dto.CropDto;
import org.example.groworders.domain.crops.model.entity.Crop;
import org.example.groworders.domain.crops.model.entity.CropStatus;
import org.example.groworders.domain.crops.model.entity.QCrop;
import org.example.groworders.domain.crops.model.entity.SaleStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CropQueryRepository {
    private final JPAQueryFactory queryFactory;
    private QCrop crop;

    public CropQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        this.crop = QCrop.crop;
    }

    public List<Crop> search(Long farmId, CropDto.Search dto) {
        return queryFactory
                .selectFrom(crop)
                .where(
                        crop.farm.id.eq(farmId),
                        typeEq(dto.getType()),
                        statusEq(dto.getStatus()),
                        saleStatusEq(dto.getSaleStatus())
                )
                .orderBy(orderByCustom(dto.getOrder()))
                .fetch();
    }

    private boolean hasText(String str) {
        return str != null && !str.isBlank();
    }

    //작물 종류
    private BooleanExpression typeEq(String type) {
        return hasText(type) ? crop.type.eq(type) : null;
    }

    //작물 상태
    private BooleanExpression statusEq(CropStatus status) {
        return status != null ? crop.status.eq(status) : null;
    }

    //판매 상태
    private BooleanExpression saleStatusEq(SaleStatus saleStatus) {
        return saleStatus != null ? crop.saleStatus.eq(saleStatus) : null;
    }

    //정렬
    private OrderSpecifier<?> orderByCustom(String order) {
        if(order == null) return crop.id.asc();

        return switch (order) {
            case "ID" -> crop.id.asc();
            case "LASTEST", "RECOMMENDED", "POPULAR" -> crop.expectedHarvestDate.desc();
            default -> crop.id.asc(); //기본 정렬 : 재고 id순
        };
    }
}
