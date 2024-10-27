package com.task.musinsa.dto;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

import java.math.BigDecimal;

public record BrandTotalPriceDto(
        Long brandId,
        String brandName,
        BigDecimal totalPrice
) {
}
