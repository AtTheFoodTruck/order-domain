package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreInfo {

    private Long userId;
    private Long storeId;
}
