package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.application.service.OrderItemService;
import com.sesac.foodtruckorder.application.service.OrderService;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.RequestItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderItemController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final Response response;

    /**
     * 장바구니에 아이템 담기
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @PostMapping("/orders/v1/carts")
    public ResponseEntity<?> addCartItem(@RequestBody RequestItemDto.RequestItem requestItem) {

        // OrderItem DTO 생성
        RequestItemDto.OrderItemDto cartItemDto = RequestItemDto.OrderItemDto.of(-1L,
                requestItem.getStoreId(),
                requestItem.getItemId(),
                requestItem.getPrice(),
                requestItem.getCount());

        orderService.addItemToCart(cartItemDto, requestItem.getStoreId(), requestItem.getUserId());

        return response.success("", HttpStatus.NO_CONTENT);
    }

    /**
     * 장바구니 아이템 삭제
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @DeleteMapping("/orders/v1/carts")
    public ResponseEntity<?> deleteOrderItem(@RequestBody RequestItemDto.RequestDeleteItem requestDeleteItem) {

        // 장바구니에 존재하는 아이템 삭제
        // 장바구니의 크기가 0 이라면 order도 삭제
        Long orderItemId = requestDeleteItem.getOrderItemId();
        Long userId = requestDeleteItem.getUserId();

        orderItemService.deleteOrderItem(orderItemId, userId);

        return response.success("", HttpStatus.NO_CONTENT);
    }

    /**
     * 장바구니 상품 수량 조절
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    public ResponseEntity<?> countControl(@RequestBody RequestItemDto.RequestCountItem requestCountItem) {

        Long orderItemId = requestCountItem.getOrderItemId();
        boolean plusMinus = requestCountItem.isPlusMinus();

        orderService.countControl(orderItemId, plusMinus);

        return response.success("");
    }



}
