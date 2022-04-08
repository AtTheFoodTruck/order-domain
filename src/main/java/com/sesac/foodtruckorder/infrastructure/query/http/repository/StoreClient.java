package com.sesac.foodtruckorder.infrastructure.query.http.repository;

import com.sesac.foodtruckorder.infrastructure.query.http.dto.ReviewStoreInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service") //apigateway에 등록된 ApplicationName
public interface StoreClient {

    // 리뷰 목록 조회에 필요한 가게 정보 조회
    @GetMapping("/items/v1/store/reviews/{storeId}")
    ReviewStoreInfo storeInfo(@RequestHeader(value="Authorization", required = true) String authorizationHeader,
                             @PathVariable("storeId") Long storeId);
}
