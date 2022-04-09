package com.sesac.foodtruckorder.infrastructure.query.http.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service") //apigateway에 등록된 ApplicationName
public interface UserClient {

}
