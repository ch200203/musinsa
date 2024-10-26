package com.task.musinsa.customer.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductPriceResponseDto {

    /**
     * 카테고리의 최저가 상품 정보
     */
    public record CategoryPriceInfo(
            String category,
            String brand,
            BigDecimal price
    ) {

    }

    /**
     * 카테고리별 최저가 상품과 총액 정보
     */
    public record TotalPriceResponse(
            List<CategoryPriceInfo> lowestPrices,
            BigDecimal totalPrice
    ) {
    }
}
