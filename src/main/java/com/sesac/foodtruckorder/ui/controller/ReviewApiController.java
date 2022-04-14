package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.application.service.ReviewService;
import com.sesac.foodtruckorder.ui.dto.Helper;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.ReviewRequestDto;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewApiController {

    private ReviewService reviewService;
    private final Response response;
    private final Helper helper;

    /**
     * 사용자) Review 목록 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    @GetMapping("/orders/v1/customer/reviews")
    public ResponseEntity<?> findReviews(HttpServletRequest request,
                                         @RequestBody ReviewRequestDto.RequestReviewList requestDto,
                                         @PageableDefault(page=0, size=5) Pageable pageable) {

        Long userId = requestDto.getUserId();

        List<ReviewResponseDto.ReviewHistoryDto> reviews = reviewService.findReviews(request, userId, pageable);

        return response.success(reviews);
    }

    /**
     * Review 생성
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    @PostMapping("/orders/v1/customer/reviews")
    public ResponseEntity<?> createReview(@RequestBody ReviewRequestDto.RequestReviewForm requestReviewForm,
                                          @Valid BindingResult results) {

        // validation check
        if (results.hasErrors()) {
            return response.invalidFields(helper.refineErrors(results));
        }

        Long userId = requestReviewForm.getUserId();
        ReviewRequestDto.ReviewDto reviewDto = ReviewRequestDto.ReviewDto.of(requestReviewForm);

        Long reviewId = reviewService.createReview(userId, reviewDto);

        return response.success(reviewId,"리뷰 저장", HttpStatus.CREATED);
    }

    /**
     * Review 삭제
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    @DeleteMapping("/orders/v1/customer/reviews")
    public ResponseEntity<?> deleteReview(@RequestBody ReviewRequestDto.DeleteReview deleteReview) {

        Long userId = deleteReview.getUserId();
        Long reviewId = deleteReview.getReviewId();

        return reviewService.deleteReview(userId, reviewId);
    }


    /**
     * 점주) 리뷰 목록 조회
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/13
     **/
    @GetMapping("/orders/v1/owner/reviews")
    public ResponseEntity<?> getStoreReviewList(HttpServletRequest request,
                                                @RequestBody ReviewRequestDto.ReqOwnerReviewList reqOwnerReviewList,
                                                @PageableDefault(page = 0, size = 10) Pageable pageable) {

        List<ReviewResponseDto.ResOwnerReviewList> storeReviewList
                = reviewService.getStoreReviewList(request, reqOwnerReviewList, pageable);

        return response.success(storeReviewList);

    }

}
