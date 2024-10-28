package com.task.musinsa.controller;

import com.task.musinsa.common.ApiResponse;
import com.task.musinsa.dto.CreateProductRequestDto;
import com.task.musinsa.dto.ProductResponseDto;
import com.task.musinsa.dto.UpdateProductRequestDto;
import com.task.musinsa.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@Valid @RequestBody CreateProductRequestDto requestDto) {
        var result = productService.createProduct(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "상품 생성 성공", result));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(@PathVariable long productId, @Valid @RequestBody UpdateProductRequestDto requestDto) {
        productService.updateProduct(productId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "상품 업데이트 성공"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "상품 삭제 성공"));
    }

}
