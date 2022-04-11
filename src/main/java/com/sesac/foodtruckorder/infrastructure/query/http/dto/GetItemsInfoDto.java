package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetItemsInfoDto {
    private Long itemId;        // 아이템 ID
    private String itemName;    // 아이템 이름
    private long itemPrice;     // 아이템 가격
    private String itemImgUrl;  // 아이템 이미지 주소

}
