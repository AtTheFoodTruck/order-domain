package com.sesac.foodtruckorder.infrastructure.query.http.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResWaitingCount {
    private int currentWaitingCount;
}