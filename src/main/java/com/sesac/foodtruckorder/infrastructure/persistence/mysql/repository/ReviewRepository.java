package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<List<Review>> findByUserId(Long userId);

    @Query("select o from Order o where o.userId=:userId and o.orderStatus=:orderStatus")
    Page<Order> findByReviews(Pageable pageable, Long userId, OrderStatus orderStatus);

    Page<Review> findByStoreIdOrderByCreatedDateDesc(Long storeId, Pageable pageable);

//    List<Review> findAllByStoreId(Iterable<Long> storeIds);

//    Page<Review> findByStoreId(Long storeId, Pageable pageable);
}
