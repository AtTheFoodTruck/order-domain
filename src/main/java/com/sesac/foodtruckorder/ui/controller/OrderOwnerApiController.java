package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.application.service.OrderService;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.OrderRequestDto;
import com.sesac.foodtruckorder.ui.dto.response.OrderResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
     * 점주 입장) 메뉴 조회
     * : 메뉴 관리 페이지
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


}
