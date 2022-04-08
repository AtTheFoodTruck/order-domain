package com.sesac.foodtruckorder.infrastructure.persistence.mysql.repository;

import com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserId(Long userId);
}
