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
    private Long id;            // ReviewID
    private String content;     // 리뷰 내용
    private Double rating;         // 리뷰 별점

    @Embedded
    private Images images;

    // User
    private Long userId;

    // Store
    private Long storeId;

    // order
    @OneToOne(fetch = LAZY, mappedBy = "review")
    private Order order;

    /** 생성 메서드 **/
    public static Review of(Long userId, Long storeId, ReviewRequestDto.ReviewDto reviewDto) {
        Review review = new Review();
        review.content = reviewDto.getContent();
        review.rating = reviewDto.getRating();
        review.rating = reviewDto.getRating();
        review.userId = userId;
        review.storeId = storeId;

        return review;
    }
}
