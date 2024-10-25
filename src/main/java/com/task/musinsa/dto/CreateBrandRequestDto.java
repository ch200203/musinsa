package com.task.musinsa.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBrandRequestDto(

        @NotBlank(message = "브랜드 명을 반드시 입력하여야 합니다.")
        String brandName
) {
}
