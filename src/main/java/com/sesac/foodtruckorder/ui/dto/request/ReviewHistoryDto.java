package com.sesac.foodtruckorder.ui.dto.request;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewHistoryDto {
    // 리뷰ID
    // 가게 ID, 가게명, 가게 사진
    // 주문내역, 주문가격

    private Long reviewId;
    private Long storeId;
    private String storeName;
    private String storeImgUrl;
    private int rating;
    private LocalDateTime reviewTime;
    private long orderPrice;
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
