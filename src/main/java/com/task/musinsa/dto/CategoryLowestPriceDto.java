package com.task.musinsa.dto;

import com.task.musinsa.domain.Category;

import java.math.BigDecimal;

public record CategoryLowestPriceDto(
        Category category,
        BigDecimal price
) {
}
