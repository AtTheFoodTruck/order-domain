package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Images {

    @Column(length= 100000000)
    private String imgUrl;
    private String imgName;

    public Images(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
