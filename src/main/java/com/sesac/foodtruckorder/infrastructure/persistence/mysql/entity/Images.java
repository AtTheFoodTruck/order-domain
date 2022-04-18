package com.sesac.foodtruckorder.infrastructure.persistence.mysql.entity;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Images {

    private String imgUrl;
    private String imgName;

    public Images(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
