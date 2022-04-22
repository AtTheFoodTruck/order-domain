package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.application.service.ReviewService;
import com.sesac.foodtruckorder.ui.dto.Helper;
import com.sesac.foodtruckorder.ui.dto.Response;
import com.sesac.foodtruckorder.ui.dto.request.ReviewRequestDto;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

@Tag(name = "ORDER", description = "리뷰 API")
@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewApiController {

    private final ReviewService reviewService;
    private final Response response;
    private final Helper helper;

    /**
     * 사용자) Review 목록 조회
     * @author jaemin
     * pathvariable
     * @version 1.0.1
     * 작성일 2022-04-08
     **/
    @Operation(summary = "고객) 고객이 작성한 리뷰 목록 조회")
    @GetMapping("/orders/v1/customer/reviews/{user_id}")
    public ResponseEntity<?> findReviews(HttpServletRequest request,
                                         @PathVariable("user_id") Long userId,
                                         @PageableDefault(page=0, size=5) Pageable pageable) {

        Page<ReviewResponseDto.ReviewHistoryDto> reviews = reviewService.findReviews(request, userId, pageable);

        ResponseReviewHistory responseReviewHistory = new ResponseReviewHistory(reviews.getContent(), reviews.getNumber(), reviews.getTotalPages());

        return response.success(responseReviewHistory);
    }

    /**
     * 사용자) 리뷰 목록 조회 응답 DTO
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
    **/
    @Data
    @NoArgsConstructor
    static class ResponseReviewHistory {
        private List<ReviewResponseDto.ReviewHistoryDto> reviewHistoryDtoList;
        private OrderCustomerApiController.ResponseOrderHistory._Page page;

        public ResponseReviewHistory(List<ReviewResponseDto.ReviewHistoryDto> reviewHistoryDtoList, int startPage, int totalPage) {
            this.reviewHistoryDtoList = reviewHistoryDtoList;
            page = new OrderCustomerApiController.ResponseOrderHistory._Page(startPage, totalPage);
        }
    }


    /**
     * Review 생성
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    @Operation(summary = "고객) 리뷰 생성")
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

        ResReviewCreateDto resReviewCreateDto = new ResReviewCreateDto(reviewId);

        return response.success(resReviewCreateDto,"리뷰 저장", HttpStatus.CREATED);
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    static class ResReviewCreateDto {
        private Long reviewId;
    }


    /**
     * Review 삭제
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022-04-08
     **/
    @Operation(summary = "고객) 리뷰 삭제")
    @DeleteMapping("/orders/v1/customer/reviews")
    public ResponseEntity<?> deleteReview(@RequestBody ReviewRequestDto.DeleteReview deleteReview) {

        Long userId = deleteReview.getUserId();
        Long reviewId = deleteReview.getReviewId();

        return reviewService.deleteReview(userId, reviewId);
    }


    /**
     * 점주) 리뷰 목록 조회
     * @author jaemin
     * get -> post
     * @version 1.0.1
     * 작성일 2022/04/13
     **/
    @Operation(summary = "점주) 리뷰 목록 조회")
    @PostMapping("/orders/v1/owner/reviews")
    public ResponseEntity<?> getStoreReviewList(HttpServletRequest request,
                                                @RequestBody ReviewRequestDto.ReqOwnerReviewList reqOwnerReviewList,
                                                @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<ReviewResponseDto.ResOwnerReviewList> storeReviewList = reviewService.getStoreReviewList(request, reqOwnerReviewList, pageable);

        ResponseStoreReview responseStoreReview = new ResponseStoreReview(storeReviewList.getContent(), storeReviewList.getNumber(), storeReviewList.getTotalPages());

        return response.success(responseStoreReview);
    }

    /**
     * 점주) 리뷰 목록 응답 dto
     * @author jaemin
     * @version 1.0.0
     * 작성일 2022/04/15
    **/
    @Data @AllArgsConstructor @NoArgsConstructor
    static class ResponseStoreReview {
        private List<ReviewResponseDto.ResOwnerReviewList> reviews;
        private OrderCustomerApiController.ResponseOrderHistory._Page page;

        public ResponseStoreReview(List<ReviewResponseDto.ResOwnerReviewList> reviews, int startPage, int endPage) {
            this.reviews = reviews;
            this.page = new OrderCustomerApiController.ResponseOrderHistory._Page(startPage, endPage);
        }
    }

}
