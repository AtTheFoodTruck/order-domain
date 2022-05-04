package com.sesac.foodtruckorder.application.service;

import com.querydsl.core.Tuple;
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
import com.sesac.foodtruckorder.infrastructure.query.http.dto.GetStoreResponse;
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

    public Page<ReviewResponseDto.ReviewHistoryDto> findReviews(HttpServletRequest request,
                                                                Long userId, Pageable pageable) {
        String authorization = request.getHeader("Authorization");

        Page<ReviewResponseDto.ReviewHistoryDto> reviewList = orderRepositoryCustom.getReviewList(pageable, userId);
        List<ReviewResponseDto.ReviewHistoryDto> reviewHistoryDtoList = reviewList.getContent();

        Set<Long> storeIds = new HashSet<>();

        for (ReviewResponseDto.ReviewHistoryDto reviewHistoryDto : reviewHistoryDtoList) {
            storeIds.add(reviewHistoryDto.getStoreId());
        }
        List<GetStoreResponse> data = storeClient.getStoreNameImageMap(authorization, storeIds).getData();
        Map<Long, String> storeInfoMap = data.stream().collect(
                Collectors.toMap(getStoreResponse -> getStoreResponse.getStoreId(), getStoreResponse -> getStoreResponse.getStoreName())
        );

        Map<Long, String> storeImageMap = data.stream().collect(
                Collectors.toMap(getStoreResponse -> getStoreResponse.getStoreId(), getStoreResponse -> getStoreResponse.getImgUrl())
        );

        for (ReviewResponseDto.ReviewHistoryDto reviewHistoryDto : reviewHistoryDtoList) {
            String storeName = storeInfoMap.get(reviewHistoryDto.getStoreId());
            String storeImgUrl = storeImageMap.get(reviewHistoryDto.getStoreId());
            reviewHistoryDto.changeStoreName(storeName);
            reviewHistoryDto.changeStoreImgUrl(storeImgUrl);
            reviewHistoryDto.changeReviewTime(reviewHistoryDto.getCreateReviewTime());
        }

        return PageableExecutionUtils.getPage(reviewHistoryDtoList, pageable, () -> reviewList.getTotalElements());
    }

    @Transactional
    public Long createReview(HttpServletRequest request, Long userId, ReviewRequestDto.ReviewDto reviewDto) {
        String authorization = request.getHeader("Authorization");

        Order findOrder = orderRepository.findByIdAndOrderStatus(reviewDto.getOrderId(), OrderStatus.COMPLETED).orElseThrow(
                () -> new OrderException("주문 내역을 찾을 수 없습니다.")
        );

        Long storeId = findOrder.getStoreId();
        Images images = new Images(reviewDto.getReviewImgUrl());
        Review review = Review.of(userId, storeId, reviewDto, images, findOrder);
        Review savedReview = reviewRepository.save(review);
        findOrder.setReview(review);

        ReviewResponseDto.ResReviewInfoDto reviewInfo = reviewRepositoryCustom.findReviewInfoByStoreId(storeId);

        storeClient.saveStoreInfos(authorization, reviewInfo);

        return savedReview.getId();
    }

    @Transactional
    public ResponseEntity<?> deleteReview(Long userId, Long reviewId) {

        Review findReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ReviewException("존재하지 않는 리뷰 정보 입니다.")
        );

        if (!findReview.getUserId().equals(userId)) {
            throw new ReviewException("해당 유저의 댓글 정보가 아닙니다");
        }

        reviewRepository.delete(findReview);

        return response.success("리뷰 삭제 완료되었습니다");
    }


    public Page<ReviewResponseDto.ResOwnerReviewList> getStoreReviewList(HttpServletRequest request, ReviewRequestDto.ReqOwnerReviewList reqOwnerReviewList,
                                                                         Pageable pageable) {
        String authorization = request.getHeader("Authorization");
        Long storeId = reqOwnerReviewList.getStoreId();

        Page<Review> reviews = reviewRepositoryCustom.findOwnerReviewList(pageable, storeId);

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
    }

}
