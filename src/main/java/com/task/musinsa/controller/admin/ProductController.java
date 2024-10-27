package com.task.musinsa.controller.admin;

import com.task.musinsa.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 1. 카테고리 별로 최저가격인 브랜드와 가격을 조회하고 총액이 얼마인지 확인 가능
    public ResponseEntity<String> getPriceByCategory() {
        return ResponseEntity.ok("최저가격");
    }

}
