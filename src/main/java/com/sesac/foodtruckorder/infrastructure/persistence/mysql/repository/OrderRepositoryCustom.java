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

    public Page<Order> findOrderHistory(Pageable pageable, Long userId) {
        Long count = queryFactory
                .select(order.countDistinct())
                .from(order)
                .where(
                        order.userId.eq(userId),
                        order.orderStatus.ne(OrderStatus.PENDING)
                ).fetchOne();

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

    public Page<ReviewResponseDto.ReviewHistoryDto> getReviewList(Pageable pageable, Long userId) {
        Long count = queryFactory.select(order.countDistinct())
                .from(order)
                .join(order.review, review)
                .where(
                        order.userId.eq(userId),
                        order.orderStatus.eq(OrderStatus.COMPLETED)
                ).fetchOne(); // 주문 정보가 4건

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
                .orderBy(review.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    public Page<Order> findPrevOrderList(OrderRequestDto.PrevOrderSearch prevOrderSearch, Pageable pageable, Long storeId) {
        Long count = queryFactory.select(order.countDistinct())
                .from(order)
                .where(
                        order.orderTime.between(prevOrderSearch.getStartDateTime(), prevOrderSearch.getEndDateTime()),
                        order.orderStatus.ne(OrderStatus.PENDING),
                        order.storeId.eq(storeId)
                ).fetchOne();

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

    public SliceImpl<Order> findOrderMain(Long storeId, OrderRequestDto.OrderSearchCondition condition, Pageable pageable) {
        LocalDateTime orderStartTime = condition.getOrderStartTime();
        LocalDateTime orderEndTime = condition.getOrderEndTime();
        List<Order> orders = queryFactory.selectFrom(order)
                .where(
                        order.storeId.eq(storeId),
                        order.orderTime.between(orderStartTime, orderEndTime),
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
