package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.QReview.review;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<ReviewResponseDto.ResReviewInfoDto> findAllByStoreId() {
        return queryFactory.select(
                        Projections.constructor(ReviewResponseDto.ResReviewInfoDto.class,
                                review.storeId,
                                review.rating.avg()
                        )
                )
                .from(review)
                .groupBy(review.storeId)
                .fetch();
    }
}
