package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;
    private int waitingNum;
    private LocalDateTime orderTime;
    private long orderPrice;

    private Long userId;

    private Long storeId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, mappedBy = "order")
    private Review review;

    public static Order of(Long userId, Long storeId, OrderStatus orderStatus, OrderItem orderItem) {
        Order order = new Order();
        order.userId = userId;
        order.storeId = storeId;
        order.orderStatus = orderStatus;
        order.addOrderItem(orderItem);

        return order;
    }

    public static Order of(Long userId, Long storeId, OrderItem... orderItem) {
        Order order = new Order();
        order.userId = userId;
        order.storeId = storeId;

        for (OrderItem item : orderItem) {
            order.addOrderItem(item);
        }

        return order;
    }

    public Order addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.orderPrice += orderItem.getTotalPrice();
        orderItem.setOrder(this);

        return this;
    }

    public Order deleteOrderItem(OrderItem findOrderItem) {
        this.orderPrice -= findOrderItem.getTotalPrice();
        this.orderItems.remove(findOrderItem);

        return this;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public void changeOrderPrice(int plusMinus, long plusPrice) {
        if (plusMinus > 0) {
            this.orderPrice += plusPrice;
        }else {
            this.orderPrice -= plusPrice;
        }
    }

    public void changeOrderStatus() {
        this.orderStatus = OrderStatus.ORDER;
        this.orderTime = LocalDateTime.now();
    }

    public Order changeAcceptOrder() {
        this.orderStatus = OrderStatus.ACCEPTED;

        return this;
    }

    public void changeRejectOrder() {
        this.orderStatus = OrderStatus.REJECTED;
    }

    public void changeCompleteOrder() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    public void changeWaitingCount(int currentWaitingCount) {
        this.waitingNum = currentWaitingCount;
    }
}
