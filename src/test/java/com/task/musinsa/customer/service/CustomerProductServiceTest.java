package com.task.musinsa.customer.service;

import com.task.musinsa.customer.dto.ProductPriceResponseDto;
import com.task.musinsa.customer.mapper.ProductMapper;
import com.task.musinsa.domain.Brand;
import com.task.musinsa.domain.Category;
import com.task.musinsa.domain.Product;
import com.task.musinsa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.task.musinsa.customer.dto.ProductPriceResponseDto.TotalPriceResponse;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private ProductMapper productMapper;

    @InjectMocks
    private CustomerProductService productService;

    private Brand brand;
    private Product product1;
    private Product product2;


    @BeforeEach
    void setUp() {
        brand = Brand.of("무신사 스탠다드");

        product1 = Product.create(brand, Category.OUTER, "미니멀자켓_블랙", BigDecimal.valueOf(75_000));
        product2 = Product.create(brand, Category.TOP, "옥스포드셔츠_화이트", BigDecimal.valueOf(15_000));

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        when(productMapper.toCategoryPriceInfo(product1)).thenReturn(new ProductPriceResponseDto.LowestCategoryPrice("아우터", "무신사 스탠다드", BigDecimal.valueOf(75_000)));
        when(productMapper.toCategoryPriceInfo(product2)).thenReturn(new ProductPriceResponseDto.LowestCategoryPrice("상의", "무신사 스탠다드", BigDecimal.valueOf(15_000)));
    }

    @Test
    void 카테고리_별로_최저가격인_브랜드와_가격을_조회하고_총액을_반환한다() {
        // when
        TotalPriceResponse response = productService.findLowestPriceByCategory();


        // then
        assertSoftly(softly -> {
            softly.assertThat(response.lowestPrices()).hasSize(2); // 아우터, 상의
            // 1. 아우터
            softly.assertThat(response.lowestPrices().get(0).category()).isEqualTo(Category.OUTER.getDisplayName());
            softly.assertThat(response.lowestPrices().get(0).brand()).isEqualTo("무신사 스탠다드");
            softly.assertThat(response.lowestPrices().get(0).price()).isEqualByComparingTo(BigDecimal.valueOf(75_000));

            // 2. 상의
            softly.assertThat(response.lowestPrices().get(1).category()).isEqualTo(Category.TOP.getDisplayName());
            softly.assertThat(response.lowestPrices().get(1).brand()).isEqualTo("무신사 스탠다드");
            softly.assertThat(response.lowestPrices().get(1).price()).isEqualByComparingTo(BigDecimal.valueOf(15_000));

            // 3. 총액
            softly.assertThat(response.totalPrice()).isEqualByComparingTo(BigDecimal.valueOf(90_000));
        });
    }
}