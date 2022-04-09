package com.sesac.foodtruckorder.ui.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RequestReviewDto {

    /**
     * Review 생성 Request Form 데이터
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public class RequestReview {
        private Long userId;        // 사용자 ID
        @NotEmpty(message = "별점을 입력해주세요.")
        private int rating;         // 별점
        private String imgName;     // 리뷰 사진 이름
        private String imgUrl;      // 리뷰 사진 url
        @NotBlank(message = "리뷰 내용을 입력해주세요.")
        @Size(min = 5, max = 100, message = "리뷰 내용은 5~100자 이내로 입력해주세요.")
        private String content;     // 리뷰 내용
        // private Long orderId;      // 주문 ID
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class _ReviewImages {
        private String imgName;
        private String imgUrl;
    }


    /**
     * Review 생성 Dto 
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
    **/
    @Getter @Builder
    public static class ReviewDto {
        private int rating;
        private String content;
        private _ReviewImages images;

        public static ReviewDto of(RequestReview requestReview) {
            _ReviewImages reviewImages = _ReviewImages.builder()
                    .imgName(requestReview.getImgName())
                    .imgUrl(requestReview.getImgUrl())
                    .build();

            return ReviewDto.builder()
                    .rating(requestReview.getRating())
                    .content(requestReview.getContent())
                    .images(reviewImages)
                    .build();
        }
    }
    /**
     * 리뷰 삭제 위한 DTO
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
    **/
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public class DeleteReview {

        private Long reviewId;
        private Long userId;
    }

    /**
     * 리뷰 목록 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
    **/
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Data
    public class RequestReviewList {
        private Long userId;
    }
}
