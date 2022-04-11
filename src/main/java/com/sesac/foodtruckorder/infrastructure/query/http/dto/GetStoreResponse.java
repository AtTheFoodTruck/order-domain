package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import lombok.Data;

/**
 * Feign Client call with Store Domain
 * @author jjaen
 * @version 1.0.0
 * 작성일 2022/04/11
**/
@Data
public class GetStoreResponse {
    private Long storeId;       // 가게ID
    private String storeName;   // 가게명
    private String imgUrl;      // 가게 이미지 주소
    private String imgName;
}
