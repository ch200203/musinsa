package com.task.musinsa.customer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.task.musinsa.common.MoneyFormatSerializer;

import java.math.BigDecimal;
import java.util.List;

public class ProductPriceResponseDto {

    /**
     * 카테고리의 최저가 브랜드 정보
     */
    public record LowestCategoryPrice(
            String category,
            String brand,
            @JsonSerialize(using = MoneyFormatSerializer.class)
            BigDecimal price
    ) {
    }

    /**
     * 카테고리별 최저가 상품과 총액 정보
     */
    public record TotalPriceResponse(
            List<LowestCategoryPrice> lowestPrices,
            @JsonSerialize(using = MoneyFormatSerializer.class)
            BigDecimal totalPrice
    ) {
    }

    /**
     * 브랜드의 카테고리별 최저가 상품 정보
     *
     * @param brand
     * @param lowestCategoryPrices
     * @param totalPrice
     */
    public record BrandCategoryPrice(
            String brand,
            List<CategoryPrice> lowestCategoryPrices,
            @JsonSerialize(using = MoneyFormatSerializer.class)
            BigDecimal totalPrice
    ) {
    }

    /**
     * 카테고리 최저가 가격
     *
     * @param category
     * @param price
     */
    public record CategoryPrice(
            String category,
            @JsonSerialize(using = MoneyFormatSerializer.class)
            BigDecimal price
    ) {
    }

    public record CategoryPriceRangeDto(
            String category,
            List<BrandPrice> lowestPrices,
            List<BrandPrice> highestPrices
    ) {}

    public record BrandPrice(
            String brand,
            @JsonSerialize(using = MoneyFormatSerializer.class)
            BigDecimal price
    ){}
}
