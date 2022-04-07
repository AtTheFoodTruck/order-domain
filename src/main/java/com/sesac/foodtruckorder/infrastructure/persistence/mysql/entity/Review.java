package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

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
    private Long id;
    private String content;
    private int rating;
    // User
    private Long userId;

    // Store
    private Long storeId;

    // Order
    @OneToOne(mappedBy = "review", fetch = LAZY)
    private Order order;

}
