package com.sesac.foodtruckorder.ui.dto.response;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderItem;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponseDto {

    @Data
    public static class OrderHistory {
        private Long orderId;
        private Long storeId;
        private String storeImgUrl;
        private String storeName;
        private long totalPrice;
        private String orderTime;
        private OrderStatus orderStatus;
        private List<_OrderItems> orderItems;
        private boolean hasReview;
        private int waitingCount;

        public static OrderHistory of(Order order) {
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.orderId = order.getId();
            orderHistory.storeId = order.getStoreId();
            orderHistory.totalPrice = order.getOrderPrice();
            orderHistory.orderTime = order.getOrderTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            orderHistory.orderStatus = order.getOrderStatus();
            orderHistory.orderItems = order.getOrderItems().stream()
                    .map(orderItem -> _OrderItems.of(orderItem))
                    .collect(Collectors.toList());
            if (order.getReview() != null) {
                orderHistory.hasReview = order.getReview().isHasReview();
            }
            orderHistory.waitingCount = order.getWaitingNum();

            return orderHistory;
        }

        public void changeStoreName(String storeName) {
            this.storeName = storeName;
        }

        public void changeStoreImgUrl(String storeImgUrl) {
            this.storeImgUrl = storeImgUrl;
        }
    }

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

    @Data
    public static class OrderMainDto{
        private List<_Order> orders;
        private boolean hasNext;

        public static OrderMainDto of(List<Order> orders, boolean hasNext) {
            OrderMainDto orderMainDto = new OrderMainDto();
            List<_Order> orderList = orders.stream().
                    map(order -> _Order.of(order))
                    .collect(Collectors.toList());

            orderMainDto.orders = orderList;
            orderMainDto.hasNext = hasNext;

            return orderMainDto;
        }
    }

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

    @Data
    public static class PrevOrderDto {
        private Long orderId;
        private OrderStatus orderStatus;
        private String orderTime;
        private long orderPrice;
        private Long userId;
        private String userName;

        private List<_PrevOrderItem> prevOrderItems;

        // 생성 메서드
        public static PrevOrderDto of(Order order) {
            PrevOrderDto prevOrderDto = new PrevOrderDto();
            prevOrderDto.orderId = order.getId();
            prevOrderDto.orderStatus = order.getOrderStatus();
            prevOrderDto.orderTime = order.getOrderTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            prevOrderDto.orderPrice = order.getOrderPrice();
            prevOrderDto.userId = order.getUserId();

            prevOrderDto.prevOrderItems = order.getOrderItems().stream()
                    .map(orderItem -> _PrevOrderItem.of(orderItem))
                    .collect(Collectors.toList());
            return prevOrderDto;
        }

        public void changeUserName(String userName) {
            this.userName = userName;
        }

        @Data
        public static class _PrevOrderItem {
            private Long orderItemId;
            private Long itemId;
            private String itemName;

            public static _PrevOrderItem of(OrderItem orderItem) {
                _PrevOrderItem prevOrderItem = new _PrevOrderItem();
                prevOrderItem.orderItemId = orderItem.getId();
                prevOrderItem.itemId = orderItem.getItemId();

                return prevOrderItem;
            }

            public void changeItemName(String itemName) {
                this.itemName = itemName;
            }
        }
    }

    @Data
    public static class OrderDetailDto {
        private Long orderId;
        private LocalDateTime orderTime;
        private OrderStatus orderStatus;
        private String storeName;
        private OrderDetailUser user;
        private List<OrderDetailItem> orderItems;

        public static OrderDetailDto of(Order order,
                                        String storeName,
                                        OrderDetailUser user,
                                        List<OrderDetailItem> orderItems) {
            OrderDetailDto orderDetailDto = new OrderDetailDto();
            orderDetailDto.orderId = order.getId();
            orderDetailDto.orderTime = order.getOrderTime();
            orderDetailDto.orderStatus = order.getOrderStatus();
            orderDetailDto.storeName = storeName;
            orderDetailDto.user = user;
            orderDetailDto.orderItems = orderItems;

            return orderDetailDto;
        }
    }

    @Data @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OrderDetailUser {
        private Long userId;
        private String userName;
        private String phoneNum;

        @Builder
        public OrderDetailUser(Long userId, String userName, String phoneNum) {
            this.userId = userId;
            this.userName = userName;
            this.phoneNum = phoneNum;
        }
    }

    @Data @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OrderDetailItem {
        private Long orderItemId;
        private Long itemId;
        private String itemName;
        private long totalPrice;
        private int count;

        @Builder
        public OrderDetailItem(Long orderItemId, Long itemId, String itemName, long totalPrice, int count) {
            this.orderItemId = orderItemId;
            this.itemId = itemId;
            this.itemName = itemName;
            this.totalPrice = totalPrice;
            this.count = count;
        }

        public static OrderDetailItem of(OrderItem orderItem) {
            OrderDetailItem orderDetailItem = new OrderDetailItem();
            orderDetailItem.orderItemId = orderItem.getId();
            orderDetailItem.itemId = orderItem.getItemId();
            orderDetailItem.totalPrice = orderItem.getTotalPrice();
            orderDetailItem.count = orderItem.getCount();

            return orderDetailItem;
        }

        public void changeItemName(String itemName) {
            this.itemName = itemName;
        }
    }

}
