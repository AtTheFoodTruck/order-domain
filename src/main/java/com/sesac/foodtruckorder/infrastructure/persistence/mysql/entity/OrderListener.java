package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;


import com.sesac.foodtruckorder.application.service.OrderSender;
import com.sesac.foodtruckorder.exception.OrderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.persistence.PostUpdate;

@Slf4j
@RequiredArgsConstructor
public class OrderListener {

    @Autowired @Lazy
    private OrderSender orderSender;

    @PostUpdate
    public void postUpdate(Order order) {
        OrderStatus orderStatus = order.getOrderStatus();
        log.info("[orderListener] {}", orderStatus);

        if (orderStatus == OrderStatus.ORDER) { // 주문 신청
            try {
                orderSender.orderPlaced(order);
            } catch (Exception e) {
                throw new OrderException(e.getMessage());
            }
        } else if (orderStatus == OrderStatus.ACCEPTED) { // 주문 수락
            try {
                orderSender.orderAccepted(order);
            } catch (Exception e) {
                throw new OrderException(e.getMessage());
            }
        }
    }
}
