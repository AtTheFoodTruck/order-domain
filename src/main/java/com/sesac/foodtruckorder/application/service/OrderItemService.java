package com.sesac.foodtruckorder.application.service;

import com.sesac.foodtruckorder.exception.OrderException;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderItem;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderItemRepository;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    /**
     * 장바구니 삭제
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
    **/
    public void deleteOrderItem(Long orderItemId, Long userId) {

        // 1. 장바구니 조회
        Order findOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.PENDING).orElseThrow(
                () -> new OrderException("장바구니 정보를 찾을 수 없습니다.")
        );

        // 2. 아이템 조회
        OrderItem findOrderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new OrderException("메뉴 정보를 찾을 수 없습니다. ")
        );

        // 3. 장바구니에서 아이템 삭제
        Order order = findOrder.deleteOrderItem(findOrderItem);

        // 4. 장바구니 크기 0이면 장바구니 삭제
        if (order.getOrderItems().size() == 0) {
            orderRepository.delete(order);
        }
    }
}
