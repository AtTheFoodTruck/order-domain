package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

import com.sesac.foodtruckorder.ui.dto.request.ReviewRequestDto;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;
    private String content;
    private Double rating;
    private boolean hasReview;  // 리뷰 작성여부

    @Embedded
    private Images images;
    private Long userId;
    private Long storeId;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    public static Review of(Long userId, Long storeId, ReviewRequestDto.ReviewDto reviewDto, Images images, Order order) {
        Review review = new Review();
        review.content = reviewDto.getContent();
        review.rating = reviewDto.getRating();
        review.images = images;
        review.userId = userId;
        review.storeId = storeId;
        review.order = order;
        review.hasReview = true;

        return review;
    }

}
