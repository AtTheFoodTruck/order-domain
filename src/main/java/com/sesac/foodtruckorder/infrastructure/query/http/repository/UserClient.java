package com.sesac.foodtruckorder.infrastructure.query.http.repository;

import com.sesac.foodtruckorder.infrastructure.query.http.dto.CreateUserDto;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetUserNameMap;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.global.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@FeignClient(name = "user-service") //apigateway에 등록된 ApplicationName
public interface UserClient {

    @GetMapping("/api/v1/info/{userIds}")
    Result<List<GetUserNameMap>> getUsers(@RequestHeader(value="Authorization", required = true) String authorizationHeader,
                                          @PathVariable("userIds") Iterable<Long> userIds);

    default Map<Long, String> getUserNameMap(String authorizationHeader, Iterable<Long> userIds) {
        if (!userIds.iterator().hasNext()) return null;
        List<GetUserNameMap> data = this.getUsers(authorizationHeader, userIds).getData();
        return data.stream()
                .collect(
                        Collectors.toMap(
                                getUserNameMap -> getUserNameMap.getUserId(), getUserNameMap -> getUserNameMap.getUserName()
                        )
                );

    }

    @GetMapping("/api/v1/info/{userId}")
    CreateUserDto userInfo(@RequestHeader(value="Authorization", required = true) String authorizationHeader,
                           @PathVariable("userId") Long userId);
}
