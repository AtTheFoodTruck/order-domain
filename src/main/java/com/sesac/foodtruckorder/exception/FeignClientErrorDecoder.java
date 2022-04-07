package com.sesac.foodtruckorder.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FeignClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String method, Response response) {

        switch (response.status()) {
            case 404:
                if(method.contains("userInfo")) {
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()), "사용자 정보를 불러오는데 실패했습니다.");
                }
                break;
            default:
                return new Exception(response.reason());
        }
        return null;
    }
}

