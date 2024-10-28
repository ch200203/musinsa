package com.task.musinsa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBrandRequestDto(
        @Size(max = 50, message = "브랜드명은 50자를 넘을 수 없습니다.")
        @NotBlank(message = "브랜드 명을 반드시 입력하여야 합니다.")
        String brandName
) {
}
