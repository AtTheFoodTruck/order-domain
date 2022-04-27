package com.sesac.foodtruckorder.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        private String reviewImgUrl;         // 리뷰Img
        private Double rating;              // 별점
        @JsonIgnore
        private LocalDateTime createReviewTime;   // 리뷰 생성 일자
        private long orderPrice;            // 총주문가격
        private String content;             // 리뷰 내용
        private String reviewTime;          // 리뷰 생성 일자
//    private List<ReviewHistory> orders = new ArrayList<>();


        public ReviewHistoryDto(Long storeId, Long reviewId, Double rating, LocalDateTime reviewTime, String reviewImgUrl, long orderPrice, String content) {
            this.storeId = storeId;
            this.reviewId = reviewId;
            this.rating = rating;
            this.createReviewTime = reviewTime;
            this.reviewImgUrl = reviewImgUrl;
            this.orderPrice = orderPrice;
            this.content = content;
        }

//        public static ReviewHistoryDto of(Order order) {
//            ReviewHistoryDto reviewHistoryDto = new ReviewHistoryDto();
//            reviewHistoryDto.reviewId = order.getReview().getId();
//            reviewHistoryDto.storeId = order.getStoreId();
//            reviewHistoryDto.rating = order.getReview().getRating();
//            reviewHistoryDto.reviewTime = order.getReview().getCreatedDate()
//                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            return reviewHistoryDto;
//        }

        public void changeStoreName(String storeName) {
            this.storeName = storeName;
        }

        public void changeStoreImgUrl(String storeImgUrl) {
            this.storeImgUrl = storeImgUrl;
        }

        public void changeReviewTime(LocalDateTime createReviewTime) {
            this.reviewTime = createReviewTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
        private String createdDate;
        private String content;

        public ResOwnerReviewList(Review review) {
            this.storeId = review.getStoreId();
            this.userId = review.getUserId();
            this.reviewImgUrl = review.getImages().getImgUrl();
            this.rating = review.getRating();
            this.createdDate = review.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            this.content = review.getContent();
        }

        public void changeUserName(String userName) {
            this.userName = userName;
        }
    }

    /**
     * ReviewResponseDto 의 설명 적기
     * @author jaemin
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
