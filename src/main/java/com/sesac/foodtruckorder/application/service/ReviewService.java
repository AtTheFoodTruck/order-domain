package com.sesac.foodtruckorder.application.service;

import com.sesac.foodtruckorder.exception.OrderException;
import com.sesac.foodtruckorder.exception.ReviewException;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Images;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Order;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.OrderStatus;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderRepository;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.OrderRepositoryCustom;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.ReviewRepository;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.ReviewRepositoryCustom;
import com.sesac.foodtruckorder.infrastructure.query.http.repository.StoreClient;
import com.sesac.foodtruckorder.infrastructure.query.http.repository.UserClient;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.ReviewRequestDto;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewRepositoryCustom reviewRepositoryCustom;
    private final OrderRepositoryCustom orderRepositoryCustom;
    private final UserClient userClient;
    private final Response response;
    private final StoreClient storeClient;

    /**
     * Review 목록 조회 - 사용자
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    public Page<ReviewResponseDto.ReviewHistoryDto> findReviews(HttpServletRequest request,
                                                                Long userId, Pageable pageable) {

        // 1. 페이징 리뷰 목록 조회
//        Page<Order> orders = reviewRepository.findByReviews(pageable, userId, OrderStatus.COMPLETED);
        Page<ReviewResponseDto.ReviewHistoryDto> reviewList = orderRepositoryCustom.getReviewList(pageable, userId);
        List<ReviewResponseDto.ReviewHistoryDto> reviewHistoryDtoList = reviewList.getContent();

        // 2. 리뷰 목록을 dto로 변환
//        List<ReviewResponseDto.ReviewHistoryDto> reviewHistoryDtoList = orders.getContent()
//                .stream()
//                .map(order -> ReviewResponseDto.ReviewHistoryDto.of(order))
//                .collect(Collectors.toList());

        // 3. 조회한 리뷰 목록(List)를 담을 Id을 Set객체를 이용해서 담을 거예여
        Set<Long> storeIds = new HashSet<>();

        // 4. dto에 개수만큼 for문을 돌려서 id를 추출하고 그 id를 Set객체에 담을거예여
        for (ReviewResponseDto.ReviewHistoryDto reviewHistoryDto : reviewHistoryDtoList) {
            storeIds.add(reviewHistoryDto.getStoreId());
        }

        // 5. Map을 받아올건데, storeClient Map<id, storeName>
        Map<Long, String> storeInfoMap = storeClient.getStoreInfoMap(request, storeIds);        // 가게 정보 조회(StoreName)
        Map<Long, String> storeImageMap = storeClient.getStoreImageInfoMap(request, storeIds);  // 가게 정보 조회(StoreImageUrl)

        // 6. for문 돌면서 dto에 value인 Name을 업데이트해줘야돼
        for (ReviewResponseDto.ReviewHistoryDto reviewHistoryDto : reviewHistoryDtoList) {
            String storeName = storeInfoMap.get(reviewHistoryDto.getStoreId());
            String storeImgUrl = storeImageMap.get(reviewHistoryDto.getStoreId());
            reviewHistoryDto.changeStoreName(storeName);
            reviewHistoryDto.changeStoreImgUrl(storeImgUrl);
            reviewHistoryDto.changeReviewTime(reviewHistoryDto.getCreateReviewTime());
        }

        return PageableExecutionUtils.getPage(reviewHistoryDtoList, pageable, () -> reviewList.getTotalElements());
//        return reviewHistoryDtoList;
    }

    /**
     * Review 등록
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
    **/
    @Transactional
    public Long createReview(Long userId, ReviewRequestDto.ReviewDto reviewDto) {

        // 1. 소비자는 주문 완료된 주문 정보를 조회한다
        // 1. userId로 order정보 꺼내오기, COMPLEDTED
        Order findOrder = orderRepository.findByIdAndOrderStatus(reviewDto.getOrderId(), OrderStatus.COMPLETED).orElseThrow(
                () -> new OrderException("주문 내역을 찾을 수 없습니다.")
        );
//        Order findOrder = orderRepository.findByUserIdAndOrderStatus(userId, OrderStatus.COMPLETED).orElseThrow(
//                () -> new OrderException("주문 내역을 찾을 수 없습니다.")
//        );

        Long storeId = findOrder.getStoreId();

        // 2. 리뷰등록화면으로 이동
        // 3. 리뷰 등록을 위한 정보를 입력(사진, 내용, 별점, userId, storeId)
        Images images = new Images(reviewDto.getReviewImgUrl());

        Review review = Review.of(userId, storeId, reviewDto, images, findOrder);

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
    @Transactional
    public ResponseEntity<?> deleteReview(Long userId, Long reviewId) {

        // 1. review 정보 조회
        Review findReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewException("존재하지 않는 리뷰 정보 입니다.")
        );

        // 2. review 정보가 userId와 일치하는지 검증
        if (!findReview.getUserId().equals(userId)) {
            throw new ReviewException("해당 유저의 댓글 정보가 아닙니다");
        }

        // 3. review 정보 삭제
        reviewRepository.delete(findReview);

        return response.success("리뷰 삭제 완료되었습니다");
    }


    /**
     * 점주) Review 목록 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/13
    **/
    public Page<ReviewResponseDto.ResOwnerReviewList> getStoreReviewList(HttpServletRequest request, ReviewRequestDto.ReqOwnerReviewList reqOwnerReviewList,
                                                                         Pageable pageable) {
        String authorization = request.getHeader("Authorization");
        Long storeId = reqOwnerReviewList.getStoreId();

        //return ReviewImgUrl, userName, rating, content, createDate
//        List<Review> reviews = reviewRepository.findByStoreIdOrderByCreatedDateDesc(storeId, pageable).getContent();
        Page<Review> reviews = reviewRepositoryCustom.findOwnerReviewList(pageable, storeId);
//        List<Review> reviews = reviewRepositoryCustom.findOwnerReviewList(pageable, storeId).getContent();

        Set<Long> userIds = new HashSet<>();
        List<ReviewResponseDto.ResOwnerReviewList> reviewLists = reviews.getContent().stream()
                .map(review -> {
                    ReviewResponseDto.ResOwnerReviewList resOwnerReviewList = new ReviewResponseDto.ResOwnerReviewList(review);
                    userIds.add(review.getUserId());
                    return resOwnerReviewList;
                }).collect(Collectors.toList());

        Map<Long, String> userNameMap = userClient.getUserNameMap(authorization, userIds);

        for (ReviewResponseDto.ResOwnerReviewList review : reviewLists) {
            review.changeUserName(userNameMap.get(review.getUserId()));
        }

        return PageableExecutionUtils.getPage(reviewLists, pageable, () -> reviews.getTotalElements());
//        return reviewLists;

    }
}
