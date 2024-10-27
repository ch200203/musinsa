package com.task.musinsa.customer.controller;

import com.task.musinsa.customer.dto.ProductPriceResponseDto;
import com.task.musinsa.customer.service.CustomerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final CustomerProductService customerProductService;

    @GetMapping("/lowest-price-by-category")
    public ResponseEntity<ProductPriceResponseDto.TotalPriceResponse> getLowestPriceByCategory() {
        var result = customerProductService.findLowestPriceByCategory();
        return ResponseEntity.ok(result);
    }


    @GetMapping("/lowest-price-by-brand")
    public ResponseEntity<ProductPriceResponseDto.BrandCategoryPrice> getLowestPriceByBrand() {
        var result = customerProductService.findLowestPriceByBrand();
        return ResponseEntity.ok(result);
    }
}
