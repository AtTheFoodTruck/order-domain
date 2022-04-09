package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.application.service.OrderService;
import com.sesac.foodtruckorder.ui.dto.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderOwnerApiController {

    private final OrderService orderService;
    private final Response response;

    /**
     * 점주 입장) 메뉴 조회
     * : 메뉴 관리 페이지
     * @author jjaen
     * @version 1.0.0
     * 작성일 2022/04/03
    **/

}
