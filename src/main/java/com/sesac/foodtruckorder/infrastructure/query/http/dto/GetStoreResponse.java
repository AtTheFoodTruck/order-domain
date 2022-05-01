package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Feign Client call with Store Domain
 * @author jjaen
 * @version 1.0.0
 * 작성일 2022/04/11
**/
@Data
@NoArgsConstructor
public class GetStoreResponse {
    private Long storeId;       // 가게ID
    private String storeName;   // 가게명
    private String imgUrl;      // 가게 이미지 주소

    public GetStoreResponse(Long storeId, String storeName, String imgUrl) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.imgUrl = imgUrl;
    }
}
