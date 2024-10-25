package com.task.musinsa.dto;

public record CreateProductRequestDto(
        long brandId,
        long categoryId,
        String productName,
        double price

) {

}
