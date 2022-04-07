package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
