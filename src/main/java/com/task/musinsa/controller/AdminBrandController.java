package com.task.musinsa.controller;

import com.task.musinsa.common.ApiResponse;
import com.task.musinsa.dto.BrandResponseDto;
import com.task.musinsa.dto.CreateBrandRequestDto;
import com.task.musinsa.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/brand")
@RequiredArgsConstructor
public class AdminBrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponseDto>> createBrand(@Valid @RequestBody CreateBrandRequestDto createBrandRequestDto) {
        var result = brandService.createBrand(createBrandRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "브랜드 생성 성공", result));
    }

}
