package com.sesac.foodtruckorder.ui.dto.response;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderItem;
import com.sesac.foodtruckorder.ui.controller.OrderCustomerApiController;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "장바구니 response DTO")
public class OrderItemResponseDto {

    @Schema(description = "장바구니 내역 조회")
    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class FetchOrderDto {
        @Schema(description = "OrderItem ID")
        @ApiModelProperty(value = "OrderItem ID")
        private Long orderItemId;
        @ApiModelProperty(value = "Item ID")
        private Long itemId;
        @ApiModelProperty(value = "Item Image")
        private String itemImgUrl;
        @ApiModelProperty(value = "Item Name")
        private String itemName;
        @ApiModelProperty(value = "Count")
        private int count;

        @ApiModelProperty(value = "Item UnitPrice")
        private long unitPrice;

        @ApiModelProperty(value = "Total Price")
        private long totalPrice;

        public static FetchOrderDto of(
                                       String itemName,
                                       String itemImgUrl,
                                       long itemUnitPrice,
                                       OrderItem orderItem) {
            FetchOrderDto fetchOrderDto = new FetchOrderDto();
            fetchOrderDto.orderItemId = orderItem.getId();
            fetchOrderDto.itemId = orderItem.getItemId();
            fetchOrderDto.itemName = itemName;
            fetchOrderDto.itemImgUrl = itemImgUrl;
            fetchOrderDto.count = orderItem.getCount();
            fetchOrderDto.unitPrice = itemUnitPrice;
            fetchOrderDto.totalPrice = orderItem.getTotalPrice();

            return fetchOrderDto;
        }
    }

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
