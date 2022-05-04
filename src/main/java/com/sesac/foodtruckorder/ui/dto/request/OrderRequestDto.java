package com.sesac.foodtruckorder.ui.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class OrderRequestDto {

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    @ToString
    public static class RequestOrderListDto {
        private Long userId;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class OrderSearchCondition {
        private Long userId;

        @Pattern(regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$",
                message = "YYYY-MM-DD 형식에 맞게 작성되지 않았습니다.")
        private String orderDate;

        public LocalDateTime getOrderStartTime() {
            LocalDate orderTime = LocalDate.parse(orderDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return orderTime.atStartOfDay(); // yyyy-MM-dd:00:00
        }

        public LocalDateTime getOrderEndTime() {
            LocalDate orderTime = LocalDate.parse(orderDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return LocalDateTime.of(orderTime, LocalTime.of(23, 59, 59)); // yyyy-MM-dd:23:59:59
        }
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class PrevOrderSearch {

        private Long userId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        @NotNull(message = "시작일은 필수 검색 조건입니다.")
        private LocalDate startDate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        @NotNull(message = "종료일은 필수 검색 조건입니다.")
        private LocalDate endDate;

        public LocalDateTime getStartDateTime() {
            return startDate.atStartOfDay();
        }

        public LocalDateTime getEndDateTime() {
            return LocalDateTime.of(endDate, LocalTime.of(23, 59, 59));
        }
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class OrderDetailSearch {
        private Long orderId;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class ChangeOrderStatus {
        private Long orderId;
    }
}
