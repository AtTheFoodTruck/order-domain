package com.sesac.foodtruckorder.ui.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

public class RequestOrderItemDto {

    /**
     * 장바구니 목록 조회 요청 DTO
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
     **/
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public class RequestOrderItemList {
        private Long userId;
    }

    /**
     * 장바구니에 아이템 담기 요청 폼 DTO
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @Data
    public class RequestItem {
        private Long itemId;    // 상품ID
        private int price;      // 상품 가격
        private int count;      // 상품 수량
        private Long userId;    // 유저ID
        private Long storeId;   // 가게ID
    }

    /**
     * 장바구니에 아이템 담기 DTO
     *
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDto {
        private Long cartItemId;    // cartItem ID
        private Long storeId;       // 가게 ID
        private Long itemId;        // 상품 ID
        private int count;          // 상품 수량
        private int unitPrice;      // 상품 단위 가격
//        private String itemName;    // 상품 Name

        // cartItem_id, item_id, price, count
        public static OrderItemDto of(Long cartItemId, Long storeId, Long itemId,
                                      int unitPrice, int count) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.cartItemId = cartItemId;
            orderItemDto.storeId = storeId;
            orderItemDto.itemId = itemId;
            orderItemDto.unitPrice = unitPrice;
            orderItemDto.count = count;

            return orderItemDto;
        }
    }

    /**
     * 장바구니에 담긴 상품 삭제 DTO
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class RequestDeleteItem {
        private Long userId;
        private Long orderItemId;
    }

    /**
     * 장바구니 수량 변경
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
    **/
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class RequestCountItem {
        private Long orderItemId;
        private boolean plusMinus;
    }

}
