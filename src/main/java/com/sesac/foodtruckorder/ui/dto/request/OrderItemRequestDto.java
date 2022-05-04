package com.sesac.foodtruckorder.ui.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

public class OrderItemRequestDto {

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class RequestOrderItemList {
        private Long userId;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class RequestItem {
        private Long itemId;
        private int price;
        private int count;
        private Long userId;
        private Long storeId;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        private Long orderItemId;
        private Long storeId;
        private Long itemId;
        private int count;
        private int unitPrice;
        public static OrderItemDto of(Long orderItemId, Long storeId, Long itemId,
                                      int unitPrice, int count) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.orderItemId = orderItemId;
            orderItemDto.storeId = storeId;
            orderItemDto.itemId = itemId;
            orderItemDto.unitPrice = unitPrice;
            orderItemDto.count = count;

            return orderItemDto;
        }
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class RequestDeleteItem {
        private Long userId;
        private Long orderItemId;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class RequestCountItem {
        private Long orderId;
        private Long orderItemId;
        private boolean plusMinus;
    }

}
