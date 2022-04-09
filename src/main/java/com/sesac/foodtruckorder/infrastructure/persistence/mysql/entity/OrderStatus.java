package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING,        // 주문대기( 장바구니 상태)
    ORDER,          // 주문 상태
    ACCEPTED,       // 주문 수락
    REJECTED,       // 주문 거절
    PROCESSING,     // 조리 상태
    COMPLETED,      // 조리 완료
    FAILED          // 취소 상태

//    PENDING("주문대기(장바구니)"),
//    PLACED("주문신청"),
//    ACCEPTED("주문수락"),
//    REJECTED("주문거절"),
//    PROCESSING("조리상태"),
//    FINISHED("조리완료료"),    FAILED("실패");
}
