package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.ui.dto.request.OrderRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Long countByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);

    Optional<Order> findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);

    @Query("select o from Order o where o.review.id=:reviewId")
    Optional<Order> findByReviewId(Long reviewId);

    Optional<Order> findByUserId(Long userId);

    @Query("select o from Order o where o.userId=:userId and o.orderStatus <> :orderStatus order by o.orderTime desc")
    Page<Order> findByUserIdAndPaging(Pageable pageable, Long userId, OrderStatus orderStatus);

    @Query("select o from Order o where o.storeId=:storeId and o.orderTime between :start and :end and o.orderStatus <> :orderStatus order by o.id desc")
    Slice<Order> findOrderMainPage(LocalDateTime start, LocalDateTime end, Long storeId, OrderStatus orderStatus, Pageable pageable);

//    @Query("select o from Order o where o.userId=:userId and o.orderStatus=:orderStatus")
//    Page<Order> findByReviews(Pageable pageable, Long userId, OrderStatus orderStatus);
}
