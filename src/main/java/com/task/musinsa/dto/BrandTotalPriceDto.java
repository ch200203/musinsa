package com.task.musinsa.dto;

import java.math.BigDecimal;

public record BrandTotalPriceDto(
        Long brandId,
        String brandName,
        BigDecimal totalPrice
) {
}
