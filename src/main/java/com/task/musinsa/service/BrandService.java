package com.task.musinsa.service;

import com.task.musinsa.domain.Brand;
import com.task.musinsa.dto.CreateBrandRequestDto;
import com.task.musinsa.exception.DuplicateNameException;
import com.task.musinsa.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional
    public Brand createBrand(CreateBrandRequestDto createBrandRequestDto) {
        validateBrandName(createBrandRequestDto.brandName());
        Brand brand = Brand.of(createBrandRequestDto.brandName());
        return brandRepository.save(brand);
    }

    private void validateBrandName(String name) {
        if (brandRepository.findByName(name).isPresent()) {
            throw new DuplicateNameException("브랜드가 이미 존재합니다: " + name);
        }
    }

}
