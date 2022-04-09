package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import lombok.Data;

@Data
public class GetStoreResponse {
    private Long storeId;
    private String storeName;
    private String imgUrl;
    private String imgName;
}
