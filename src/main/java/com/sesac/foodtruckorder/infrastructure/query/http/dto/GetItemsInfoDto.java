package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import lombok.Data;

@Data
public class GetItemsInfoDto {
    private Long itemId;
    private String itemName;
    private long itemPrice;
    private String itemImgUrl;

}
