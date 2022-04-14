package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import com.sesac.foodtruckorder.ui.dto.response.ReviewResponseDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReviewRepositoryCustomTest {

    @Autowired ReviewRepositoryCustom reviewRepositoryCustom;
    @Autowired ReviewRepository reviewRepository;
    @Autowired JPAQueryFactory queryFactory;
    @PersistenceContext EntityManager em;

    @Rollback(value = false)
    @Test
    public void 쿼리디에스알_테스트() {
        //given
        Review 리뷰_내용1 = Review.builder()
                .content("리뷰 내용1")
                .rating(2D)
                .storeId(1L)
                .build();

        Review 리뷰_내용2 = Review.builder()
                .content("리뷰 내용2")
                .rating(4D)
                .storeId(1L)
                .build();

        Review 리뷰_내용3 = Review.builder()
                .content("리뷰 내용3")
                .rating(3D)
                .storeId(1L)
                .build();

        reviewRepository.save(리뷰_내용1);
        reviewRepository.save(리뷰_내용2);
        reviewRepository.save(리뷰_내용3);

        queryFactory = new JPAQueryFactory(em);

        //when
        List<ReviewResponseDto.ResReviewInfoDto> allByStoreId = reviewRepositoryCustom.findAllByStoreId();
        allByStoreId.stream()
                .forEach(resReviewInfoDto -> System.out.println("resReviewInfoDto.getAvgRating() = " + resReviewInfoDto.getAvgRating()));


        //then

    }
}