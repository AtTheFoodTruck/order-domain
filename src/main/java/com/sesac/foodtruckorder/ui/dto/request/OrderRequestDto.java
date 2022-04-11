package com.sesac.foodtruckorder.ui.dto.request;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class OrderRequestDto {

    /**
     * 주문 내역 조회 요청 form (사용자)
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-10
     **/
    @Data
    public static class RequestOrderListDto {
        private Long userId;
    }

    /**
     * 주문 정보 조회 (점주)
     *
     * @author jjaen
     * @version 1.0.0
     * 작성일 2022/04/11
     **/
    @Data
    public static class OrderSearchCondition {
        private Long userId;

        @Pattern(regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$",
                message = "YYYY-MM-DD 형식에 맞게 작성되지 않았습니다.")
        private String orderDate;

        // 영업 시작 시간
        public LocalDateTime getOrderStartTime() {
            LocalDate orderTime = LocalDate.parse(orderDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return orderTime.atStartOfDay(); // yyyy-MM-dd:00:00
        }

        // 영업 종료 시간
        public LocalDateTime getOrderEndTime() {
            LocalDate orderTime = LocalDate.parse(orderDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return LocalDateTime.of(orderTime, LocalTime.of(23, 59, 59)); // yyyy-MM-dd:23:59:59
        }


    }


}
