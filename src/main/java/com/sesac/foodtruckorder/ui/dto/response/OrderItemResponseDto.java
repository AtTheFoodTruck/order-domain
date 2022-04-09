package com.sesac.foodtruckorder.ui.dto.response;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderItem;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetStoreResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OrderItemResponseDto {

    /**
     * 장바구니 목록 조회 응답 Dto
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
    **/
    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class FetchOrderDto {
        private Long orderItemid;       // OrderItemId
        private String storeName;       // 푸드트럭 명
        private String storeImgUrl;     // 푸드트럭 이미지
        private Long itemId;            // 아이템 ID
        private String itemName;        // 아이템 이름
        private int count;              // 개수
        private long totalPrice;        // 금액

        public static FetchOrderDto of(String storeName,
                                       String storeImgUrl,
                                       String itemName,
                                       OrderItem orderItem) {
            FetchOrderDto fetchOrderDto = new FetchOrderDto();
            fetchOrderDto.orderItemid = orderItem.getId();
            fetchOrderDto.storeName = storeName;
            fetchOrderDto.storeImgUrl = storeImgUrl;
            fetchOrderDto.itemId = orderItem.getItemId();
            fetchOrderDto.itemName = itemName;
            fetchOrderDto.count = orderItem.getCount();
            fetchOrderDto.totalPrice = orderItem.getTotalPrice();

            return fetchOrderDto;
        }

    }
}
