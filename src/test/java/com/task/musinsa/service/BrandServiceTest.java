package com.task.musinsa.service;

import com.task.musinsa.domain.Brand;
import com.task.musinsa.dto.CreateBrandRequestDto;
import com.task.musinsa.exception.DuplicateNameException;
import com.task.musinsa.repository.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    void 브랜드_생성에_성공한다() {
        // given
        CreateBrandRequestDto request = new CreateBrandRequestDto("무신사 스탠다드");
        Brand brand = Brand.of(request.brandName());
        ReflectionTestUtils.setField(brand, "id", 1L); // Id 세팅


        when(brandRepository.findByName(request.brandName())).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        // when
        var response = brandService.createBrand(request);

        // then
        assertSoftly(it -> {
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
        });
        verify(brandRepository).findByName("무신사 스탠다드");
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    void 중복된_브랜드_생성에_실패한다() {
        // given
        CreateBrandRequestDto request = new CreateBrandRequestDto("무신사 스탠다드");
        when(brandRepository.findByName(request.brandName()))
                .thenReturn(Optional.of(Brand.of("무신사 스탠다드")));

        // when & then
        assertThatThrownBy(() -> brandService.createBrand(request))
                .isInstanceOf(DuplicateNameException.class)
                .hasMessageContaining("브랜드가 이미 존재합니다: 무신사 스탠다드");

        verify(brandRepository).findByName("무신사 스탠다드");
        verify(brandRepository, never()).save(any(Brand.class));
    }
}
