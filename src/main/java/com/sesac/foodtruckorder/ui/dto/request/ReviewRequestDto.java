package com.sesac.foodtruckorder.ui.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class ReviewRequestDto {

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class RequestReviewForm {
        private Long userId;
        private Long orderId;
        @NotEmpty(message = "별점을 입력해주세요.")
        private Double rating;
        private String imgName;
        private String reviewImgUrl;
        @NotBlank(message = "리뷰 내용을 입력해주세요.")
        @Size(min = 5, max = 100, message = "리뷰 내용은 5~100자 이내로 입력해주세요.")
        private String content;
    }

    @Getter @Builder
    public static class ReviewDto {
        private Long orderId;
        private Double rating;
        private String content;
        private String reviewImgUrl;

        public static ReviewDto of(RequestReviewForm requestReviewForm) {
            return ReviewDto.builder()
                    .orderId(requestReviewForm.getOrderId())
                    .rating(requestReviewForm.getRating())
                    .content(requestReviewForm.getContent())
                    .reviewImgUrl(requestReviewForm.getReviewImgUrl())
                    .build();
        }
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class DeleteReview {
        private Long reviewId;
        private Long userId;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class RequestReviewList {
        private Long userId;
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public static class ReqOwnerReviewList {
        private Long storeId;
        private Long userId;
    }
}
