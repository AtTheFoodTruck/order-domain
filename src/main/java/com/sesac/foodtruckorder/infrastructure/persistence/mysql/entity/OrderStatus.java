package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("주문 대기(장바구니)"),
    ORDER("주문 상태"),
    ACCEPTED("주문 수락"),
    REJECTED("주문 거절"),
    COMPLETED("조리 완료"),

    FAILED("취소 상태");
    private String message;

    OrderStatus(String message) {
        this.message = message;
    }
}