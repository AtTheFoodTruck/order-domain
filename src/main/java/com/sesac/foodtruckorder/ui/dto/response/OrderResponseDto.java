package com.sesac.foodtruckorder.ui.dto.response;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderItem;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponseDto {

    /**
     * 주문 내역 조회 응답 DTO
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-10
     **/
    @Data
    public static class OrderHistory {
        private Long orderId;			    // 주문 ID
        private Long storeId;               // 가게 ID
        private String storeImgUrl;		    // 가게 이미지 URL
        private String storeName;		    // 가게 이름
        private long totalPrice;		    // 총 주문 가격
        private LocalDateTime orderTime;    // 주문 날짜
        private OrderStatus orderStatus;    // 주문 상태
        private List<_OrderItems> orderItems;   // 아이템 목록
        // 대기번호

        public static OrderHistory of(Order order) {
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.orderId = order.getId();
            orderHistory.totalPrice = order.getOrderPrice();
            orderHistory.orderTime = order.getOrderTime();
            orderHistory.orderStatus = order.getOrderStatus();
            orderHistory.orderItems = order.getOrderItems().stream()
                    .map(orderItem -> _OrderItems.of(orderItem))
                    .collect(Collectors.toList());
            //대기번호

            return orderHistory;
        }

        public void changeStoreName(String storeName) {
            this.storeName = storeName;
        }

        public void changeStoreImgUrl(String storeImgUrl) {
            this.storeImgUrl = storeImgUrl;
        }
    }

    /**
     * 주문 내역 조회에 사용될 아이템 목록 dto
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/11
    **/
    @Data
    public static class _OrderItems {
        private Long orderItemId;
        private Long itemId;
        private String itemName;

        public static _OrderItems of(OrderItem orderItem) {
            _OrderItems orderItems = new _OrderItems();
            orderItems.orderItemId = orderItem.getId();
            orderItems.itemId = orderItem.getItemId();

            return orderItems;
        }

        public void changeItemName(String itemName) {
            this.itemName = itemName;
        }
    }

}
