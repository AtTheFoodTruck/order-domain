package com.sesac.foodtruckorder.application.service;

import com.sesac.foodtruckorder.exception.OrderException;
import com.sesac.foodtruckorder.exception.ReviewException;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderRepository;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.ReviewRepository;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.ReviewStoreInfo;
import com.sesac.foodtruckorder.infrastructure.query.http.repository.StoreClient;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.RequestReviewDto;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final Response response;
    private final StoreClient storeClient;

    /**
     * Review 목록 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    public ReviewResponseDto.ReviewListDto findReviews(HttpServletRequest request,
                                                       Long userId) {

        String authorization = request.getHeader("Authorization");

        Review findReview = reviewRepository.findByUserId(userId).orElseThrow(
                () -> new ReviewException("댓글이 존재하지 않습니다.")
        );

        // 리뷰 관리 페이지
        // 가게사진, 가게명, 주문 총금액, 별점, 리뷰작성일자, 리뷰내용
        // 리뷰 - 별점, 리뷰작성일자, 리뷰내용
        // 가게 - 가게사진, 가게명
        // 주문 - 주문 총금액

        // 리뷰
        ReviewResponseDto.ReviewContentDto reviewContentDto = ReviewResponseDto.ReviewContentDto.of(findReview);

        // 가게
        ReviewStoreInfo reviewStoreInfo = storeClient.storeInfo(authorization, findReview.getStoreId());
        log.info("Return 받은 user 객체의 값 : {}", reviewStoreInfo);

        // 주문
        Order findOrder = orderRepository.findByReviewId(findReview.getId()).orElseThrow(
                () -> new OrderException("주문 정보가 존재하지 않습니다.")
        );

        // return Dto
        return ReviewResponseDto.ReviewListDto.builder()
                .imgUrl(reviewStoreInfo.getImgUrl())
                .imgName(reviewStoreInfo.getImgName())
                .storeName(reviewStoreInfo.getStoreName())
                .orderPrice(findOrder.getOrderPrice())
                .reviewContentDto(reviewContentDto)
                .build();
    }

    /**
     * Review 등록
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
    **/
    public Long createReview(Long userId, RequestReviewDto.ReviewDto reviewDto) {

        // 1. 소비자는 주문 완료된 주문 정보를 조회한다
        // 1. userId로 order정보 꺼내오기, COMPLEDTED
        Order findOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.COMPLETED).orElseThrow(
                () -> new OrderException("주문 내역을 찾을 수 없습니다.")
        );

        Long storeId = findOrder.getStoreId();

        // 2. 리뷰등록화면으로 이동
        // 3. 리뷰 등록을 위한 정보를 입력(사진, 내용, 별점, userId, storeId)
        Review review = Review.of(userId, storeId, reviewDto);
        // 4. 리뷰 등록
        Review savedReview = reviewRepository.save(review);

        // 5. 리뷰 등록이 완료되면, 주문 정보에 review 정보가 업데이트된다
        findOrder.setReview(review);

        return savedReview.getId();
    }

    /**
     * Review 삭제
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
    **/
    public ResponseEntity<?> deleteReview(Long userId, Long reviewId) {

        // 1. review 정보 조회
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewException("존재하지 않는 리뷰 정보 입니다.")
        );

        // 2. review 정보가 userId와 일치하는지 검증
        if (!findReview.getUserId().equals(userId)) {
            return response.fail("해당 유저의 댓글 정보가 아닙니다", HttpStatus.BAD_REQUEST);
        }

        // 3. review 정보 삭제
        reviewRepository.delete(findReview);

        return response.success("리뷰 삭제 완료되었습니다");
    }
}
