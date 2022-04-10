package com.sesac.foodtruckorder.ui.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

public class OrderRequestDto {

    /**
     * 주문 내역 조회 요청 form (사용장)
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-10
     **/
    @Data
    public static class RequestOrderListDto {
        private Long userId;
    }
}
