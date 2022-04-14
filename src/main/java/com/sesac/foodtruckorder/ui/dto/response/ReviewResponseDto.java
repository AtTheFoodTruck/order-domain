package com.sesac.foodtruckorder.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import lombok.*;

import java.time.LocalDateTime;

public class ReviewResponseDto {

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
        private Double rating;                 // 별점
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

        public void changeStoreImgUrl(String storeImgUrl) {
            this.storeImgUrl = storeImgUrl;
        }
    }

    /**
     * 점주) Review 목록 조회 응답 DTO
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/13
     **/
    @Data @NoArgsConstructor
    public static class ResOwnerReviewList {
        private Long storeId;
        @JsonIgnore
        private Long userId;
        private String reviewImgUrl;
        private String userName;
        private Double rating;
        private LocalDateTime createdDate;
        private String content;

        public ResOwnerReviewList(Review review) {
            this.storeId = review.getStoreId();
            this.userId = review.getUserId();
            this.reviewImgUrl = review.getImages().getImgUrl();
            this.rating = review.getRating();
            this.createdDate = review.getCreatedDate();
            this.content = review.getContent();
        }

        public void changeUserName(String userName) {
            this.userName = userName;
        }
    }

    /**
     * ReviewResponseDto 의 설명 적기
     * @author jjaen
     * @version 1.0.0
     * 작성일 2022/04/14
     **/
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResReviewInfoDto {
        private Long storeId;
        private Double avgRating;
    }
}
