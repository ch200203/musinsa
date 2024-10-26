package com.task.musinsa.customer.controller;

import com.task.musinsa.customer.dto.ProductPriceResponseDto;
import com.task.musinsa.customer.service.CustomerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/customer/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final CustomerProductService customerProductService;

    @GetMapping("/lowest-price-by-category")
    public ResponseEntity<ProductPriceResponseDto.TotalPriceResponse> getLowestPriceByCategory() {
        ProductPriceResponseDto.TotalPriceResponse result = customerProductService.findLowestPriceByCategory();
        return ResponseEntity.ok(result);
    }
}
