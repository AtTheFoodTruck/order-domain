package com.sesac.foodtruckorder.ui.dto.response;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import lombok.*;

import java.time.LocalDateTime;

public class ResponseReviewDto {

    /**
     * Review 목록 조회 응답 Dto
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-09
    **/
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReviewHistoryDto {
        private Long reviewId;              // 리뷰ID
        private Long storeId;               // 가게ID
        private String storeName;           // 가게명
        private String storeImgUrl;         // 가게Img
        private int rating;                 // 별점
        private LocalDateTime reviewTime;   // 리뷰 생성 일자
        private long orderPrice;            // 총주문가격
//    private List<ReviewHistory> orders = new ArrayList<>();

        public static ReviewHistoryDto of(Order order) {
            ReviewHistoryDto reviewHistoryDto = new ReviewHistoryDto();
            reviewHistoryDto.reviewId = order.getReview().getId();
            reviewHistoryDto.storeId = order.getStoreId();
            reviewHistoryDto.rating = order.getReview().getRating();
            reviewHistoryDto.reviewTime = order.getReview().getCreatedDate();
            reviewHistoryDto.orderPrice = order.getOrderPrice();

            return reviewHistoryDto;
        }

        public void changeStoreName(String storeName) {
            this.storeName = storeName;
        }
    }

}
