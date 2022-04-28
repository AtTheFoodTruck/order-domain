package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderResponseDto 의 설명 적기
 * @author jaemin
 * @version 1.0.0
 * 작성일 2022/04/11
 **/
@Data @NoArgsConstructor @AllArgsConstructor
public class GetStoreInfoByUserId {
    private Long storeId;
    private String storeName;
}