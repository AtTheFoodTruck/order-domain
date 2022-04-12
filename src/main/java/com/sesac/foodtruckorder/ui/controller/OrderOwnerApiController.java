package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.application.service.OrderService;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.ui.dto.Helper;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.OrderRequestDto;
import com.sesac.foodtruckorder.ui.dto.response.OrderResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderOwnerApiController {

    private final OrderService orderService;
    private final Response response;

    /**
     * 점주) 주문 조회 페이지
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/03
    **/
    @GetMapping("/orders/v1/owner/oder")
    public ResponseEntity<?> orderMainPage(HttpServletRequest request,
                                           @Valid OrderRequestDto.OrderSearchCondition condition,
                                           Pageable pageable) {

        OrderResponseDto.OrderMainDto orderMainPage = orderService.findOrderMainPage(request, condition, pageable);

        // 화면에 맞게 dto 생성
        OrderMainResponse orderMainResponse = new OrderMainResponse(orderMainPage);

        return response.success(orderMainResponse);
    }

    /** Dto 시작 **/
    /*******************************************************************************************************************/
    /**
     * 주문 조회 페이지(점주) 화면에 fit한 dto 생성
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
    **/
    @Data @NoArgsConstructor
    static class OrderMainResponse {
        private List<_OrderResponse> orderList;

        public OrderMainResponse(OrderResponseDto.OrderMainDto orderMainDto) {
            this.orderList = orderMainDto.getOrders().stream()
                    .map(order -> new _OrderResponse(order))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 주문 조회 페이지(점주) 화면에 fit한 dto 생성
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
     **/
    @Data @NoArgsConstructor
    static class _OrderResponse {
        private Long orderId;
        private String orderTime;
        private String orderStatus;
        private String userName;
        private List<_OrderItemResponse> orderItems;

        public _OrderResponse(OrderResponseDto._Order order) {
            this.orderId = order.getOrderId();
            this.orderTime = order.getOrderTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.orderStatus = order.getOrderStatus().name();
            this.userName = order.getUserName();
            this.orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new _OrderItemResponse(orderItem))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 주문 조회 페이지(점주) 화면에 fit한 dto 생성
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
     **/
    static class _OrderItemResponse {
        private String itemName;

        public _OrderItemResponse(OrderResponseDto._OrderItem orderItem) {
            this.itemName = orderItem.getItemName();
        }
    }
    /*******************************************************************************************************************/
    /** Dto 끝 **/

    /**
     * 이전 주문 내역 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
     **/
    @GetMapping("/orders/v1/owner/prev/order")
    public ResponseEntity<?> getPrevOrderList(HttpServletRequest request,
                                              @Valid @RequestBody OrderRequestDto.PrevOrderSearch prevOrderSearch,
                                              BindingResult results,
                                              @PageableDefault(page = 0, size = 10) Pageable pageable) {
        // validation check
        if (results.hasErrors()) {
            response.invalidFields(Helper.refineErrors(results));
        }

        // 시작일, 종료일 validation check
        LocalDate startDate = prevOrderSearch.getStartDate();
        LocalDate endDate = prevOrderSearch.getEndDate();

        if (startDate.isAfter(endDate)) {
            response.fail("시작일은 종료일보다 클 수 없습니다.", HttpStatus.BAD_REQUEST);
        }

        List<OrderResponseDto.PrevOrderDto> prevOrderList = orderService.findPrevOrderList(request, prevOrderSearch, pageable);

        // orderId, orderStatus, orderTime, orderItemId, itemName, orderTotalPrice, userName
        PrevOrderResponse prevOrderResponse = new PrevOrderResponse(prevOrderList);

        return response.success(prevOrderResponse);
    }
    /** Dto 시작 **/
    /*******************************************************************************************************************/

    static class PrevOrderResponse {
        private List<_Order> orders;

        public PrevOrderResponse(List<OrderResponseDto.PrevOrderDto> prevOrderdtoList) {
            this.orders = prevOrderdtoList.stream().map(orders -> new _Order(orders)).collect(Collectors.toList());
        }

        static class _Order {
            private Long orderId;
            private OrderStatus orderStatus;
            private LocalDateTime orderTime;
            private long orderPrice;
            private String userName;
            private List<_OrderItem> orderItems;

            public _Order(OrderResponseDto.PrevOrderDto orders) {
                this.orderId = orders.getOrderId();
                this.orderStatus = orders.getOrderStatus();
                this.orderTime = orders.getOrderTime();
                this.orderPrice = orders.getOrderPrice();
                this.userName = orders.getUserName();
                orderItems = orders.getPrevOrderItems().stream().map(
                        prevOrderItem -> new _OrderItem(prevOrderItem)
                ).collect(Collectors.toList());
            }

            static class _OrderItem {
                private Long orderItemId;
                private String itemName;

                public _OrderItem(OrderResponseDto.PrevOrderDto._PrevOrderItem prevOrderItem) {
                    this.orderItemId = prevOrderItem.getOrderItemId();
                    this.itemName = prevOrderItem.getItemName();
                }
            }

        }

    }
    /*******************************************************************************************************************/
    /** Dto 끝 **/

    /**
     * 주문 상세 보기
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/12
     **/
    @GetMapping("/orders/v1/owner/order-detail")
    public ResponseEntity<?> getOrderDetail(HttpServletRequest request,
                                            @RequestBody OrderRequestDto.OrderDetailSearch orderDetailSearch) {

        OrderResponseDto.OrderDetailDto orderDetail = orderService.findOrderDetail(request, orderDetailSearch);

        return response.success(orderDetail);
    }
}
