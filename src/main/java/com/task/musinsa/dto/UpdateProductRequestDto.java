package com.task.musinsa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateProductRequestDto(
        @NotBlank(message = "새로운 상품명은 필수입니다.")
        String newName,

        @Positive(message = "새로운 가격은 0보다 커야 합니다.")
        BigDecimal newPrice
) {
}
