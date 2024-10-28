package com.task.musinsa.dto;

import com.task.musinsa.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateProductRequestDto(
        @NotNull(message = "브랜드 ID를 반드시 입력하여야 합니다.")
        Long brandId,

        @NotNull(message = "카테고리 명을 반드시 입력하여야 합니다.")
        Category category,

        @NotBlank(message = "상품명을 반드시 입력하여야 합니다.")
        String productName,

        @NotNull(message = "가격을 반드시 입력하여야 합니다.")
        @Positive(message = "가격은 0보다 커야 합니다.")
        BigDecimal price
) {
}
