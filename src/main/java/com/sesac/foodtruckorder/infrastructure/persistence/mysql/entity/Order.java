package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private int waitingNum;             // 대기번호
    private LocalDateTime orderTime;    // 주문 시간
    private long orderPrice;            // 주문 가격
    private boolean hasReview;          // 리뷰 작성 여부

    // User
    private Long userId;

    // Stor
    private Long storeId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Review
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public static Order of(Long userId, Long storeId, OrderItem orderItem) {
        Order order = new Order();
        order.userId = userId;
        order.storeId = storeId;
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

    /** 장바구니 품목 추가 **/
    public Order addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.orderPrice += orderItem.getTotalPrice();
        orderItem.setOrder(this);

        return this;
    }

    /** 장바구니 삭제 **/
    public Order deleteOrderItem(OrderItem findOrderItem) {
        this.orderPrice -= findOrderItem.getPrice();
        this.orderItems.remove(findOrderItem);

        return this;
    }
}
