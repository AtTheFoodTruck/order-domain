package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Long countByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);

    Optional<Order> findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);


}
