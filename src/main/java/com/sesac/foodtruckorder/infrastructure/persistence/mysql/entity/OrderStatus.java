package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING,        // 주문대기( 장바구니 상태)
    ORDER,          // 주문 상태
    PROCESSING,     // 조리 상태
    COMPLETED,      // 조리 완료 상태
    CANCEL          // 취소 상태
}
