package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.application.service.ReviewService;
import com.sesac.foodtruckorder.ui.dto.Helper;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.RequestReviewDto;
import com.sesac.foodtruckorder.ui.dto.request.ReviewHistoryDto;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private ReviewService reviewService;
    private final Response response;
    private final Helper helper;

    /**
     * Review 정보 조회 - 사용자 입장
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    @GetMapping("/orders/v1/reviews")
    public ResponseEntity<?> findReviews(HttpServletRequest request,
                                         @RequestBody RequestReviewDto.RequestReviewList requestReviewList,
                                         Pageable pageable) {

        Long userId = requestReviewList.getUserId();

        List<ReviewHistoryDto> reviews = reviewService.findReviews(request, userId, pageable);

        return response.success(reviews);
    }
1
    /**
     * Review 등록
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    @PostMapping("/orders/v1/reviews")
    public ResponseEntity<?> createReview(@RequestBody RequestReviewDto.RequestReview requestReview,
                                          @Valid BindingResult results) {

        // validation check
        if (results.hasErrors()) {
            return response.invalidFields(helper.refineErrors(results));
        }

        Long userId = requestReview.getUserId();
        RequestReviewDto.ReviewDto reviewDto = RequestReviewDto.ReviewDto.of(requestReview);

        Long reviewId = reviewService.createReview(userId, reviewDto);

        return response.success(reviewId,"리뷰 저장", HttpStatus.CREATED);
    }

    /**
     * Review 삭제
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    @DeleteMapping("/orders/v1/reviews")
    public ResponseEntity<?> deleteReview(@RequestBody RequestReviewDto.DeleteReview deleteReview) {

        Long userId = deleteReview.getUserId();
        Long reviewId = deleteReview.getReviewId();

        return reviewService.deleteReview(userId, reviewId);
    }
}
