package com.sesac.foodtruckorder.ui.dto.response;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewResponseDto {

    @Data @Builder
    public static class ReviewListDto {
        // 가게사진, 가게명, 주문 총금액, 별점, 리뷰작성일자, 리뷰내용
        private String imgUrl;
        private String imgName;
        private String storeName;
        private long orderPrice;
        private ReviewContentDto reviewContentDto;

    }

    @Getter @Builder
    public static class ReviewContentDto {
        private int rating;
        private String createReviewDate;
        private String reviewContent;

        public static ReviewContentDto of(Review review) {
            LocalDateTime createdDate = review.getCreatedDate();
            String createReviewDate = createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            return ReviewContentDto.builder()
                    .rating(review.getRating())
                    .createReviewDate(createReviewDate)
                    .reviewContent(review.getContent())
                    .build();
        }
    }

}
