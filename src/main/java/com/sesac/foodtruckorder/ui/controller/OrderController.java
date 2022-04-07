package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.application.service.OrderService;
import com.sesac.foodtruckorder.ui.dto.Helper;
import com.sesac.foodtruckorder.ui.dto.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;
    private final Response response;

    /**
     * 점주 입장) 메뉴 조회
     * : 메뉴 관리 페이지
     * @author jjaen
     * @version 1.0.0
     * 작성일 2022/04/03
    **/
    /*@GetMapping("/orders/v1/menu")
    public ResponseEntity<?> getItems(@Valid @RequestBody , BindingResult results) {
        // validation 검증
        if (results.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(results));
        }


        return response.success("");
    }*/



}
