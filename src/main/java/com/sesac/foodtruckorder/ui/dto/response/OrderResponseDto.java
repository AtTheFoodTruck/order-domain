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

    /**
     * OrderResponseDto 의 설명 적기
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/11
     **/
    @Data
    public static class GetStoreInfoByUserId {
        private Long storeId;
        private String storeName;
    }

    /**
     * OrderMainDto
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
    **/
    @Data
    public static class OrderMainDto{
        private List<_Order> orders;

        public static OrderMainDto of(List<Order> orders) {
            OrderMainDto orderMainDto = new OrderMainDto();
            List<_Order> orderList = orders.stream().
                    map(order -> _Order.of(order))
                    .collect(Collectors.toList());

            orderMainDto.orders = orderList;

            return orderMainDto;
        }
    }

    /**
     * OrderMainDto - List<Order> dto
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
    **/
    @Data
    public static class _Order {
        private Long orderId;
        private Long userId;
        private OrderStatus orderStatus;
        private LocalDateTime orderTime;
        private List<_OrderItem> orderItems;
        private String userName;

        public static _Order of(Order order) {
            _Order orderDto = new _Order();
            orderDto.orderId = order.getId();
            orderDto.userId = order.getUserId();
            orderDto.orderStatus = order.getOrderStatus();
            orderDto.orderTime = order.getOrderTime();
            orderDto.orderItems = order.getOrderItems().stream()
                    .map(orderItem -> _OrderItem.of(orderItem))
                    .collect(Collectors.toList());

            return orderDto;
        }

        public void changeUserName(String userName) {
            this.userName = userName;
        }
    }

    /**
     * OrderMainDto - List<_Order> - List<_OrderItem>
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
    **/
    @Data
    public static class _OrderItem {
        private Long orderItemId;
        private Long itemId;
        private String itemName;

        public static _OrderItem of(OrderItem orderItem) {
            _OrderItem orderItemDto = new _OrderItem();
            orderItemDto.orderItemId = orderItem.getId();
            orderItemDto.itemId = orderItem.getItemId();

            return orderItemDto;
        }

        public void changeItemName(String itemName) {
            this.itemName = itemName;
        }
    }

    /**
     * feign client with user Domain - 유저 정보 조회
     * userId, userName
     * @author
     * @version 1.0.0
     * 작성일 2022/04/12
    **/
    @Data
    public static class GetUserNameMap {
        private Long userId;
        private String userName;
    }


}
