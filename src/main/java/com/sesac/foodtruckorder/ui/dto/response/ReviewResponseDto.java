package com.sesac.foodtruckorder.ui.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReviewHistoryDto {
        private Long reviewId;
        private Long storeId;
        private String storeName;
        private String storeImgUrl;
        private String reviewImgUrl;
        private Double rating;
        @JsonIgnore
        private LocalDateTime createReviewTime;
        private long orderPrice;
        private String content;
        private String reviewTime;

        public ReviewHistoryDto(Long storeId, Long reviewId, Double rating, LocalDateTime reviewTime, String reviewImgUrl, long orderPrice, String content) {
            this.storeId = storeId;
            this.reviewId = reviewId;
            this.rating = rating;
            this.createReviewTime = reviewTime;
            this.reviewImgUrl = reviewImgUrl;
            this.orderPrice = orderPrice;
            this.content = content;
        }

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResReviewInfoDto {
        private Long storeId;
        private Double avgRating;
    }
}
