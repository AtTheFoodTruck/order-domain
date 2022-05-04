package com.sesac.foodtruckorder.ui.controller;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository.ReviewRepositoryCustom;
import com.sesac.foodtruckorder.infrastructure.query.http.dto.global.Result;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewRepositoryCustom reviewRepositoryCustom;

    @Operation(summary = "별점 평균 조회")
    @GetMapping("/api/v1/reviews/{storeId}")
    public ResponseEntity<Result> getReviewInfo(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                                @PathVariable("storeId") List<Long> storeIds) {

        List<ReviewResponseDto.ResReviewInfoDto> content = reviewRepositoryCustom.findAllByStoreIds(storeIds);

        return ResponseEntity.ok(Result.createSuccessResult(content));
    }

    @Data
    static class GetReviewInfoDto {
        private Long storeId;
        private Double avgRate;

        public GetReviewInfoDto(Long storeId, Double avgRate) {
            this.storeId = storeId;
            this.avgRate = avgRate;
        }
    }

}
