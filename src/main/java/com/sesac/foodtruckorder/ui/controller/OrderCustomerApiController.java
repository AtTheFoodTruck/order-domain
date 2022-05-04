package com.sesac.foodtruckorder.ui.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.sesac.foodtruckorder.application.service.OrderItemService;
import com.sesac.foodtruckorder.application.service.OrderService;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.OrderItemRequestDto;
import com.sesac.foodtruckorder.ui.dto.request.OrderRequestDto;
import com.sesac.foodtruckorder.ui.dto.response.OrderItemResponseDto;
import com.sesac.foodtruckorder.ui.dto.response.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "ORDER", description = "고객 장바구니, 주문 API")
@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderCustomerApiController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final Response response;

    /**
     * 장바구니 내역 조회
     * @author jaemin
     * pathvariable
     * @version 1.0.1
     * 작성일 2022-04-09
     **/
    @Operation(summary = "고객) 장바구니 내역 조회")
    @GetMapping("/orders/v1/customer/carts/{user_id}")
    public ResponseEntity<?> fetchOrder(HttpServletRequest request,
                                        @PathVariable("user_id") Long userId,
                                        @PageableDefault(page = 0, size = 10) Pageable pageable) {

        OrderItemResponseDto.ResCartListDto resCartListDto = orderService.fetchOrder(request, userId);

//        OrderItemResponseDto.ResCartListDto resCartListDto = new OrderItemResponseDto.ResCartListDto();

        return response.success(resCartListDto);
    }


    /**
     * 장바구니에 아이템 담기
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @Operation(summary = "고객) 장바구니 아이템 추가")
    @PostMapping("/orders/v1/customer/carts")
    public ResponseEntity<?> addCartItem(@RequestBody OrderItemRequestDto.RequestItem requestItem) {

        Long storeId = requestItem.getStoreId();
        Long userId = requestItem.getUserId();

        // OrderItem DTO 생성
        OrderItemRequestDto.OrderItemDto orderItemDto = OrderItemRequestDto.OrderItemDto.of(-1L,
                requestItem.getStoreId(),
                requestItem.getItemId(),
                requestItem.getPrice(),
                requestItem.getCount());

        orderService.addItemToCart(orderItemDto, storeId, userId);

        return response.success("", HttpStatus.CREATED);
    }

    /**
     * 장바구니 아이템 삭제
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @Operation(summary = "고객) 장바구니 아이템 삭제")
    @DeleteMapping("/orders/v1/customer/carts/{orderItemId}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable("orderItemId") Long orderItemId) {
            //@RequestBody OrderItemRequestDto.RequestDeleteItem requestDeleteItem) {

        // 장바구니에 존재하는 아이템 삭제
        // 장바구니의 크기가 0 이라면 order도 삭제
//        Long orderItemId = requestDeleteItem.getOrderItemId();
//        Long userId = requestDeleteItem.getUserId();

        orderItemService.deleteOrderItem(orderItemId);

        return response.success("", HttpStatus.NO_CONTENT);
    }

    /**
     * 장바구니 상품 수량 조절
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @Operation(summary = "고객) 장바구니 아이템 수량 변경")
    @PatchMapping("/orders/v1/customer/carts")
    public ResponseEntity<?> countControl(@RequestBody OrderItemRequestDto.RequestCountItem requestCountItem) {

        Long orderId = requestCountItem.getOrderId();
        Long orderItemId = requestCountItem.getOrderItemId();
        boolean plusMinus = requestCountItem.isPlusMinus();

        orderService.countControl(orderId, orderItemId, plusMinus);

        return response.success("", HttpStatus.NO_CONTENT);
    }

    /**
     * 고객) 주문 내역 조회
     * @author jaemin
     * get -> post 변경, uri 변경, dto, orderId 제거
     * @version 1.0.1
     * 작성일 2022-04-10
     **/
    @Operation(summary = "고객) 주문 내역 조회")
    @GetMapping("/orders/v1/customer/order/list/{user_id}")
    public ResponseEntity<?> findOrderHistory(HttpServletRequest request,
                                              @PathVariable("user_id") Long userId,
                                              @PageableDefault(page = 0, size = 5) Pageable pageable) {

        Page<OrderResponseDto.OrderHistory> orderHistory = orderService.findOrderHistory(pageable, request, userId);

        ResponseOrderHistory responseOrderHistory =
                new ResponseOrderHistory(orderHistory.getContent(), orderHistory.getNumber(), orderHistory.getTotalPages());

        return response.success(responseOrderHistory);
    }

    /**
     * 주문 내역 조회 response dto
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
     **/
    @Data @AllArgsConstructor @NoArgsConstructor
    public static class ResponseOrderHistory {
        private List<OrderResponseDto.OrderHistory> orderHistoryList;
        private _Page page;

        public ResponseOrderHistory(List<OrderResponseDto.OrderHistory> orderHistoryList, int startPage, int totalPage) {
            this.orderHistoryList = orderHistoryList;
            this.page = new _Page(startPage, totalPage);
        }

//        @Data
//        static class _OrderHistoryDto {
//            private Long orderId;			    // 주문 ID
//            private Long storeId;               // 가게 ID
//            private String storeImgUrl;		    // 가게 이미지 URL
//            private String storeName;		    // 가게 이름
//            private long totalPrice;		    // 총 주문 가격
//            private LocalDateTime orderTime;    // 주문 날짜
//            private OrderStatus orderStatus;    // 주문 상태
//            private List<OrderResponseDto._OrderItems> orderItems;   // 아이템 목록
//        }


        @Data @AllArgsConstructor
        public static class _Page {
            int startPage;
            int totalPage;
        }
    }

    /**
     * 주문 생성
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/13
     **/
    @Operation(summary = "고객) 주문 생성")
    @PostMapping("/orders/v1/customer/order")
//    public ResponseEntity<?> saveOrder(@RequestBody OrderRequestDto.RequestOrderListDto requestOrderListDto) {
    public ResponseEntity<?> saveOrder(@RequestBody CreateOrders createOrders) {
//        Long userId = requestOrderListDto.getUserId();
        Long userId = createOrders.getUserId();

        orderService.saveOrder(userId);

        return response.success("주문 생성이 완료되었습니다.", HttpStatus.CREATED);
    }

    @JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
    @Data
    public static class CreateOrders {
        private Long userId;
    }

    
}
