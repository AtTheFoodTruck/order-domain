package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.QReview.review;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<ReviewResponseDto.ResReviewInfoDto> findAllByStoreIds(List<Long> storeIds) {
        return queryFactory.select(
                        Projections.constructor(ReviewResponseDto.ResReviewInfoDto.class,
                                review.storeId,
                                review.rating.avg()
                        )
                )
                .from(review)
                .where(review.storeId.in(storeIds))
                .groupBy(review.storeId)
                .fetch();
    }

    public Page<Review> findOwnerReviewList(Pageable pageable, Long storeId) {
        Long count = queryFactory.select(review.countDistinct())
                .from(review)
                .where(
                        review.storeId.eq(storeId)
                ).fetchOne();

        List<Review> content = queryFactory.selectFrom(review)
                .where(
                        review.storeId.eq(storeId)
                )
                .orderBy(review.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    public ReviewResponseDto.ResReviewInfoDto findReviewInfoByStoreId(Long storeId) {

        return queryFactory.select(
                        Projections.constructor(ReviewResponseDto.ResReviewInfoDto.class,
                                review.storeId,
                                review.rating.avg()
                        )
                )
                .from(review)
                .where(review.storeId.eq(storeId))
                .groupBy(review.storeId)
                .fetchOne();
    }
}
