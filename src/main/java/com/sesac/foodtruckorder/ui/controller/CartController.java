package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.application.service.OrderService;
import com.sesac.foodtruckorder.ui.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CartController {

    private final OrderService orderService;
    private final Response response;


    /**
     * 장바구니 담기
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-07
     **/
    @PostMapping("/orders/v1/carts")
    public ResponseEntity<?> addCartItem() {

        // 사용자가 상품을 선택 장바구니에 담기 (userId)

        // 사용자에게 장바구니가 존재하는지 체크(cartId)

        // 장바구니가 있다면 존재하는 장바구니에 상품을 add

        // 만약에 장바구니에 동일한 상품이 이미 존재한다면? 상품의 수량을 update해준다

        // 장바구니가 없다면 장바구니를 생성 후 상품을 add

        //

        return response.success("");
    }
}
