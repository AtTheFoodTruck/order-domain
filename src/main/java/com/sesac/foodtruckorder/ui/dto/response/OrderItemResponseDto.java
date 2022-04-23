package com.sesac.foodtruckorder.ui.dto.response;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderItem;
import com.sesac.foodtruckorder.ui.controller.OrderCustomerApiController;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "장바구니 response DTO")
public class OrderItemResponseDto {

    /**
     * 장바구니 목록 조회 응답 Dto
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
    **/
    @Schema(description = "장바구니 내역 조회")
    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class FetchOrderDto {
        @Schema(description = "OrderItem ID")
        @ApiModelProperty(value = "OrderItem ID")
        private Long orderItemId;       // OrderItemId
//        @ApiModelProperty(value = "Store Name")
//        private String storeName;       // 푸드트럭 명
//        @ApiModelProperty(value = "Store Image URL")
//        private String storeImgUrl;     // 푸드트럭 이미지
        @ApiModelProperty(value = "Item ID")
        private Long itemId;            // 아이템 ID
        @ApiModelProperty(value = "Item Name")
        private String itemName;        // 아이템 이름
        @ApiModelProperty(value = "Item Image")
        private String itemImgUrl;        // 아이템 이름
        @ApiModelProperty(value = "Count")
        private int count;              // 개수
        @ApiModelProperty(value = "Total Price")
        private long totalPrice;        // 금액

        public static FetchOrderDto of(
//                                        String storeName,
//                                       String storeImgUrl,
                                       String itemName,
                                       String itemImgUrl,
                                       OrderItem orderItem) {
            FetchOrderDto fetchOrderDto = new FetchOrderDto();
            fetchOrderDto.orderItemId = orderItem.getId();
//            fetchOrderDto.storeName = storeName;
//            fetchOrderDto.storeImgUrl = storeImgUrl;
            fetchOrderDto.itemId = orderItem.getItemId();
            fetchOrderDto.itemName = itemName;
            fetchOrderDto.itemImgUrl = itemImgUrl;
            fetchOrderDto.count = orderItem.getCount();
            fetchOrderDto.totalPrice = orderItem.getTotalPrice();

            return fetchOrderDto;
        }
    }

    /**
     * 최종 장바구니 내역 조회 response dto
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/22
     **/
    @Data @NoArgsConstructor
    public static class ResCartListDto {
        private String storeName;           // 가게명
        private List<FetchOrderDto> cartList;   // 장바구니 내역 List

        public ResCartListDto(String storeName, List<FetchOrderDto> list) {
            this.storeName = storeName;
            this.cartList = list;
        }
    }
}
