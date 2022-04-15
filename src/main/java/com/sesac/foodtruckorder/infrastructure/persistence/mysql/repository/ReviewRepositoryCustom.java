package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

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

    /**
     * 별점 평균 구하기
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
    **/
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

    /**
     * 점주) 리뷰 목록 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
     **/
    public Page<Review> findOwnerReviewList(Pageable pageable, Long storeId) {
        // 카운트 쿼리
        Long count = queryFactory.select(review.countDistinct())
                .from(review)
                .where(
                        review.storeId.eq(storeId)
                ).fetchOne();

        // 데이터 쿼리
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
}
