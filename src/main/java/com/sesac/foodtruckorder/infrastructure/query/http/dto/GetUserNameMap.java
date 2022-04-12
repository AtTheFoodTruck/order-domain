package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import lombok.Data;

/**
 * feign client with user Domain - 유저 정보 조회
 * userId, userName
 * @author jaemin
 * @version 1.0.0
 * 작성일 2022/04/12
 **/
@Data
public class GetUserNameMap {
    private Long userId;
    private String userName;
}
