package com.sesac.foodtruckorder.infrastructure.query.http.repository;

import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetItemsInfoDto;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetStoreResponse;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetStoreInfoByUserId;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.ResWaitingCount;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.global.Result;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@FeignClient(name = "item-service") //apigateway에 등록된 ApplicationName
public interface StoreClient {

    @GetMapping("/api/v1/store/reviews/{storeIds}")
    Result<List<GetStoreResponse>> getStoreNameImageMap(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                                        @PathVariable("storeIds") Iterable<Long> storeIds);
    @GetMapping("/api/v1/store/cart/{storeId}")
    Result<GetStoreResponse> getStore(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                      @PathVariable("storeId") String storeId);
    @GetMapping("/api/v1/item/{itemId}")
    Result<List<GetItemsInfoDto>> getItem(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                          @PathVariable("itemId") Iterable<Long> itemIds);
    @GetMapping("/api/v1/store/{userId}")
    Result<GetStoreInfoByUserId> getStoreInfoByUserId(@RequestHeader(value="Authorization", required = true) String authorizationHeader,
                                                      @PathVariable("userId") Long userId);
    @PostMapping("/api/v1/store/review")
    void saveStoreInfos(@RequestHeader(value="Authorization", required = true) String authorizationHeader,
                       @RequestBody ReviewResponseDto.ResReviewInfoDto storeInfo);
    @PostMapping("/api/v1/store/waiting/{storeId}")
    ResWaitingCount saveWaitingCount(@RequestHeader(value="Authorization", required = true) String authorizationHeader,
                                     @PathVariable("storeId") Long storeId);

    default Map<Long, String> getStoreInfoMap(HttpServletRequest request, Set<Long> storeIds) {
        String authorization = request.getHeader("Authorization");

        if( !storeIds.iterator().hasNext()) return null;
        List<GetStoreResponse> storeResponses = this.getStoreNameImageMap(authorization, storeIds).getData();
        return storeResponses.stream()
                .collect(
                        Collectors.toMap(getStoreResponse -> getStoreResponse.getStoreId(),
                                getStoreResponse -> getStoreResponse.getStoreName())
                );
    }

    default Map<Long, String> getStoreImageInfoMap(HttpServletRequest request, Set<Long> storeIds) {
        String authorization = request.getHeader("Authorization");

        if( !storeIds.iterator().hasNext()) return null;
        List<GetStoreResponse> storeResponses = this.getStoreNameImageMap(authorization, storeIds).getData();
        return storeResponses.stream()
                .collect(
                        Collectors.toMap(getStoreResponse -> getStoreResponse.getStoreId(),
                                getStoreResponse -> getStoreResponse.getImgUrl())
                );
    }

    default Map<Long, String> getItemInfoMap(HttpServletRequest request, Set<Long> itemIds) {
        String authorization = request.getHeader("Authorization");

        if( !itemIds.iterator().hasNext()) return null;
        List<GetItemsInfoDto> itemInfoMap = this.getItem(authorization, itemIds).getData();
        return itemInfoMap.stream()
                .collect(
                        Collectors.toMap(getItemsInfoDto -> getItemsInfoDto.getItemId(),
                                getItemsInfoDto -> getItemsInfoDto.getItemName())
                );
    }

}
