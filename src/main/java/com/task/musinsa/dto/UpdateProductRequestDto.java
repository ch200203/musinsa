package com.task.musinsa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateProductRequestDto(
        @NotBlank(message = "상품명을 반드시 입력하여야 합니다.")
        String newName,

        @Positive(message = "가격은 0보다 커야 합니다.")
        BigDecimal newPrice
) {
}
