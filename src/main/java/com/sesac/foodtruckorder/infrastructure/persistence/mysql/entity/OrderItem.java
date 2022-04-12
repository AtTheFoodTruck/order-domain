package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderItem_id")
    private Long id;        // OrderItem_id
    private Long itemId;    // Item_id
    private long price;      // 주문 가격
    private int count;      // 주문 수량

    // Order
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /** Order 연관관계 세팅 **/
    public void setOrder(Order order){
        this.order = order;
    }

    /** 생성 메서드 **/
    public static OrderItem of(Long itemId, long price, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.itemId = itemId;
        orderItem.price = price;
        orderItem.count = count;

        return orderItem;
    }

    /** 주문상품 전체 가격조회 **/
    public long getTotalPrice() {
        return this.price * this.count;
    }

    /** 상품 수량 증가 **/
    public void changeCount(int count) {
        this.count = count;
    }

}
