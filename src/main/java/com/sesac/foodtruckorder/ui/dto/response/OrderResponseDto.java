package com.sesac.foodtruckorder.ui.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

public class OrderResponseDto {

    /**
     * 주문 내역 조회 응답 DTO
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-10
     **/
    @Data
    public static class orderHistory {
        private Long orderId;			    // 주문 ID
        private String storeImgUrl;		    // 가게 이미지 URL
        private String storeName;		    // 가게 이름
        private long totalPrice;		    // 총 주문 가격
        private LocalDateTime orderDate;    // 주문 날짜
        private List<_OrderItems> orders;   // 아이템 목록
        // 대기번호
    }
}
