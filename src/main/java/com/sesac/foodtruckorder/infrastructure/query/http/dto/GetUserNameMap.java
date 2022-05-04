package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import lombok.Data;

@Data
public class GetUserNameMap {
    private Long userId;
    private String userName;
}
