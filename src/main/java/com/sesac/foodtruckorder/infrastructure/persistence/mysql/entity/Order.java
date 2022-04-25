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
    private int waitingNum;             // 대기번호
    private LocalDateTime orderTime;    // 주문 시간
    private long orderPrice;            // 주문 가격
//    private boolean hasReview;          // 리뷰 작성 여부

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
    @OneToOne(fetch = LAZY, mappedBy = "order")
//    @JoinColumn(name = "review_id")
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

    /** 장바구니 품목 추가 **/
    public Order addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.orderPrice += orderItem.getTotalPrice();
        orderItem.setOrder(this);

        return this;
    }

    /** 장바구니 삭제 **/
    public Order deleteOrderItem(OrderItem findOrderItem) {
        this.orderPrice -= findOrderItem.getTotalPrice();
        this.orderItems.remove(findOrderItem);

        return this;
    }

    /** 연관관계 세팅 **/
    public void setReview(Review review) {
        this.review = review;
    }

    /** 가격 변경**/
    public void changeOrderPrice(int plusMinus, long plusPrice) {
        if (plusMinus > 0) {
            this.orderPrice += plusPrice;
        }else {
            this.orderPrice -= plusPrice;
        }
    }

    /** 주문 상태 변경 -> 주문 상태 **/
    public void changeOrderStatus() {
        this.orderStatus = OrderStatus.ORDER;
        this.orderTime = LocalDateTime.now();
    }

    /** 주문 상태 변경 -> 주문 접수 **/
    public Order changeAcceptOrder() {
        this.orderStatus = OrderStatus.ACCEPTED;

        return this;
    }

    /** 주문 상태 변경 -> 주문 거절 **/
    public void changeRejectOrder() {
        this.orderStatus = OrderStatus.REJECTED;
    }

    /** 주문 상태 변경 -> 조리 상태 완료 **/
    public void changeCompleteOrder() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    /** 대기번호 저장**/
    public void changeWaitingCount(int currentWaitingCount) {
        this.waitingNum = currentWaitingCount;
    }
}
