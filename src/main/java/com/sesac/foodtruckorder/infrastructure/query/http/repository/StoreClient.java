package com.sesac.foodtruckorder.infrastructure.query.http.repository;

import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetItemsInfoDto;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetStoreResponse;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetStoreInfoByUserId;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.global.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@FeignClient(name = "item-service") //apigateway에 등록된 ApplicationName
public interface StoreClient {

    // 리뷰 목록 조회에 필요한 가게 정보 조회
    /**
     * 리뷰 목록 조회(가게입장), 주문 내역 조회
     * 가게 정보 조회 - 다중
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/11
    **/
    @GetMapping("/items/v1/store/reviews")
    Result<List<GetStoreResponse>> getStoreNameImageMap(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                                        @PathVariable("storeId") Iterable<Long> storeIds);

    /**
     * 장바구니 내역 조회
     * 가게 정보 조회 - 단건
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/11
    **/
    @GetMapping("/items/v1/store/{storeId}")
    Result<GetStoreResponse> getStore(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                      @PathVariable("storeId") String storeId);

    /**
     * 장바구니 내역 조회, 아이템 정보 조회
     * itemId, itemName
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/11
    **/
    @GetMapping("/items/v1/item/{itemId}")
    Result<List<GetItemsInfoDto>> getItem(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                          @PathVariable("itemId") Iterable<Long> itemIds);

    /**
     * 가게 정보 조회
     * using by 주문 조회 페이지 (점주)
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/11
     **/
    @GetMapping("/items/v1/store/{userId}")
    Result<GetStoreInfoByUserId> getStoreInfoByUserId(String authorization, @PathVariable("userId") Long userId);


    /**
     * 가게 이름 정보 조회
     * using by 리뷰 목록 조회
     * 재사용성을 위해 default 메서드 이용
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
    **/
    // 재사용성을 위해 default 메서드 이용
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

    /**
     * 가게 이미지 주소 정보 조회
     * using by 장바구니 목록 조회, 주문 내역 조회
     * 재사용성을 위해 default 메서드 이용
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
     **/
    // 재사용성을 위해 default 메서드 이용
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



    /**
     * 아이템 정보 조회
     * 재사용성을 위해 default 메서드 이용
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
     **/
    // 재사용성을 위해 default 메서드 이용
    default Map<Long, String> getItemInfoMap(HttpServletRequest request, Set<Long> storeIds) {
        String authorization = request.getHeader("Authorization");

        if( !storeIds.iterator().hasNext()) return null;
        List<GetItemsInfoDto> itemInfoMap = this.getItem(authorization, storeIds).getData();
        return itemInfoMap.stream()
                .collect(
                        Collectors.toMap(getItemsInfoDto -> getItemsInfoDto.getItemId(),
                                getItemsInfoDto -> getItemsInfoDto.getItemName())
                );
    }
}
