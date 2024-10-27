package com.task.musinsa.controller.admin;

import com.task.musinsa.common.ApiResponse;
import com.task.musinsa.dto.CreateBrandRequestDto;
import com.task.musinsa.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponseDto>> createBrand(@Valid @RequestBody CreateBrandRequestDto createBrandRequestDto) {
        var result = brandService.createBrand(createBrandRequestDto);
        var responseDto = new BrandResponseDto(result.getId(), result.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, responseDto));
    }

    record BrandResponseDto(
            long id,
            String name
    ) {}


}
