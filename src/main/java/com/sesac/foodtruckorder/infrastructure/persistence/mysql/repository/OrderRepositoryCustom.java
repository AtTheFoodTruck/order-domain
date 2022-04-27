package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.QOrder;
import com.sesac.foodtruckorder.ui.dto.request.OrderRequestDto;
import com.sesac.foodtruckorder.ui.dto.response.OrderResponseDto;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.QOrder.order;
import static com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.QReview.review;

@Slf4j
@RequiredArgsConstructor
@Repository
public class OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * 사용자) 주문 내역 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
    **/
    public Page<Order> findOrderHistory(Pageable pageable, Long userId) {
        // 카운트 쿼리
        Long count = queryFactory
                .select(order.countDistinct())
                .from(order)
                .where(
                        order.userId.eq(userId),
                        order.orderStatus.ne(OrderStatus.PENDING)
                ).fetchOne();

        // 데이터 쿼리
        List<Order> content = queryFactory.selectFrom(order)
                .where(
                        order.userId.eq(userId),
                        order.orderStatus.ne(OrderStatus.PENDING)
                )
                .orderBy(order.orderTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    /**
     * 사용자) 리뷰 목록 조회의 주문 정보
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
     **/
    public Page<ReviewResponseDto.ReviewHistoryDto> getReviewList(Pageable pageable, Long userId) {
        // 1. storeImgUrl, storeName, order.orderPrice, order.orderTime, review.content, review.rating
        // 카운트 쿼리
        Long count = queryFactory.select(order.countDistinct())
                .from(order)
                .join(order.review, review)
                .where(
                        order.userId.eq(userId),
                        order.orderStatus.eq(OrderStatus.COMPLETED)
                ).fetchOne(); // 주문 정보가 4건

        // 데이터 쿼리
        List<ReviewResponseDto.ReviewHistoryDto> content = queryFactory.select(
                        Projections.constructor(ReviewResponseDto.ReviewHistoryDto.class,
                                review.storeId,
                                review.id,
                                review.rating,
                                review.createdDate,
                                review.images.imgUrl,
                                order.orderPrice,
                                review.content
                                ))
                .from(order)
                .join(order.review, review)
                .where(
                        order.userId.eq(userId),
                        order.orderStatus.eq(OrderStatus.COMPLETED)

                )
//                .orderBy(order.orderTime.desc())
                .orderBy(review.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    /**
     * 점주) 이전 주문 목록 조회 - Page
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
    **/
    public Page<Order> findPrevOrderList(OrderRequestDto.PrevOrderSearch prevOrderSearch, Pageable pageable, Long storeId) {
        // 카운트 쿼리
        Long count = queryFactory.select(order.countDistinct())
                .from(order)
                .where(
                        order.orderTime.between(prevOrderSearch.getStartDateTime(), prevOrderSearch.getEndDateTime()),
                        order.orderStatus.ne(OrderStatus.PENDING),
                        order.storeId.eq(storeId)
                ).fetchOne();

        // 데이터 쿼리
        List<Order> content = queryFactory.selectFrom(order)
                .where(
                        order.orderTime.between(prevOrderSearch.getStartDateTime(), prevOrderSearch.getEndDateTime()),
                        order.orderStatus.ne(OrderStatus.PENDING),
                        order.storeId.eq(storeId)
                ).orderBy(order.orderTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    /**
     * 주문 접수 페이지
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
    **/
    public SliceImpl<Order> findOrderMain(Long storeId, OrderRequestDto.OrderSearchCondition condition, Pageable pageable) {
        List<Order> orders = queryFactory.selectFrom(order)
                .where(
                        order.storeId.eq(storeId),
                        order.orderTime.between(condition.getOrderStartTime(), condition.getOrderEndTime()),
                        order.orderStatus.ne(OrderStatus.PENDING)
                )
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .distinct()
                .fetch();

        boolean hasNext = false;
        if (orders.size() > pageable.getPageSize()) {
            orders.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(orders, pageable, hasNext);
    }

}
