package com.task.musinsa.dto;

import com.task.musinsa.domain.Category;

import java.math.BigDecimal;

public record CreateProductRequestDto(
        long brandId,
        Category category,
        String productName,
        BigDecimal price
) {
}
