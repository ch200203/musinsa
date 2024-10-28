package com.task.musinsa.customer.service;

import com.task.musinsa.domain.Brand;
import com.task.musinsa.domain.Category;
import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.BrandTotalPriceDto;
import com.task.musinsa.dto.CategoryLowestPriceDto;
import com.task.musinsa.dto.ProductPriceResponseDto;
import com.task.musinsa.exception.CustomException;
import com.task.musinsa.exception.ErrorCode;
import com.task.musinsa.exception.InvalidCategoryException;
import com.task.musinsa.mapper.ProductMapper;
import com.task.musinsa.repository.ProductQueryRepository;
import com.task.musinsa.repository.ProductRepository;
import com.task.musinsa.service.CustomerProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.task.musinsa.dto.ProductPriceResponseDto.BrandPrice;
import static com.task.musinsa.dto.ProductPriceResponseDto.LowestCategoryPrice;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductQueryRepository productQueryRepository;

    @Spy
    private ProductMapper productMapper;

    @InjectMocks
    private CustomerProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void init() {
        Brand brand = Brand.of("무신사 스탠다드");
        product1 = Product.create(brand, Category.OUTER, "미니멀자켓_블랙", BigDecimal.valueOf(75000));
        product2 = Product.create(brand, Category.TOP, "옥스포드셔츠_화이트", BigDecimal.valueOf(15000));
    }

    @Nested
    class 카테고리_별_최저가격_브랜드와_가격을_조회_및_총액_확인 {
        @BeforeEach
        void setUp() {
            lenient().when(productRepository.findAll()).thenReturn(List.of(product1, product2));
            lenient().when(productMapper.toLowestCategoryPrice(any())).thenAnswer(it -> {
                Product product = it.getArgument(0);
                return new LowestCategoryPrice(
                        product.getCategory().getDisplayName(),
                        product.getBrand().getName(),
                        product.getPrice()
                );
            });
        }

        @Test
        void 카테고리_별로_최저가격인_브랜드와_가격을_조회하고_총액을_반환한다() {
            // when
            var response = productService.findLowestPriceByCategory();

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


    @Nested
    class 단일_브랜드로_전체_카테고리_상품을_구매_시_최저가격_브랜드와_총액_확인 {

        private BrandTotalPriceDto brandTotalPriceDto;
        private List<CategoryLowestPriceDto> lowestProductsByBrand;

        @BeforeEach
        void setUp() {
            brandTotalPriceDto = new BrandTotalPriceDto(4L, "D", BigDecimal.valueOf(36_100));

            lowestProductsByBrand = List.of(
                    new CategoryLowestPriceDto(Category.OUTER, BigDecimal.valueOf(10_100)),
                    new CategoryLowestPriceDto(Category.TOP, BigDecimal.valueOf(5_100)),
                    new CategoryLowestPriceDto(Category.PANTS, BigDecimal.valueOf(3_000)),
                    new CategoryLowestPriceDto(Category.SNEAKERS, BigDecimal.valueOf(9_500)),
                    new CategoryLowestPriceDto(Category.BAG, BigDecimal.valueOf(2_500)),
                    new CategoryLowestPriceDto(Category.HAT, BigDecimal.valueOf(1_500)),
                    new CategoryLowestPriceDto(Category.SOCKS, BigDecimal.valueOf(2_400)),
                    new CategoryLowestPriceDto(Category.ACCESSORY, BigDecimal.valueOf(2_000))
            );
        }

        @Test
        void 브랜드의_카테고리별_최저가_정상조회() {
            // given
            when(productQueryRepository.findLowestPriceByBrand()).thenReturn(Optional.ofNullable(brandTotalPriceDto));
            when(productQueryRepository.findLowestProductByBrand(anyLong())).thenReturn(lowestProductsByBrand);

            // when
            ProductPriceResponseDto.BrandCategoryPrice result = productService.findLowestPriceByBrand();

            // then
            assertSoftly(softly -> {
                softly.assertThat(result.brand()).isEqualTo("D");
                softly.assertThat(result.totalPrice()).isEqualByComparingTo(BigDecimal.valueOf(36_100));
                softly.assertThat(result.lowestCategoryPrices()).hasSize(lowestProductsByBrand.size());
            });
        }

        @Test
        void 브랜드의_상품_목록이_존재하지_않는_경우_예외() {
            // given
            when(productQueryRepository.findLowestPriceByBrand()).thenReturn(Optional.ofNullable(brandTotalPriceDto));
            when(productQueryRepository.findLowestProductByBrand(anyLong())).thenReturn(List.of());

            // when & then
            assertThatThrownBy(() -> productService.findLowestPriceByBrand())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("해당 브랜드에서 모든 카테고리의 최저가 상품을 찾을 수 없습니다.");
        }

        @Test
        void 브랜드들의_카테고리별_최저가_상품_목록이_없는_경우_예외() {
            when(productQueryRepository.findLowestPriceByBrand()).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.findLowestPriceByBrand())
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.BRAND_NOT_FOUND.getMessage());
        }

    }


    @Nested
    class 카테고리별_최저_최고가격_브랜드_상품가격_조회 {

        @Test
        void 카테고리별_최저가_최고가_조회_성공() {
            String categoryName = "상의";
            Category category = Category.fromDisplayName(categoryName);

            var lowestPrices = new BrandPrice(product1.getBrand().getName(), product1.getPrice());
            var highestPrices = new BrandPrice(product2.getBrand().getName(), product2.getPrice());

            var lowestPricesDto = List.of(new BrandPrice(product1.getBrand().getName(), product1.getPrice()));
            var highestPricesDto = List.of(new BrandPrice(product2.getBrand().getName(), product2.getPrice()));

            when(productQueryRepository.findLowestPriceByCategory(category)).thenReturn(lowestPrices);
            when(productQueryRepository.findHighestPriceByCategory(category)).thenReturn(highestPrices);

            // when
            var result = productService.findPriceRangeCategory(categoryName);

            // then
            assertSoftly(softly -> {
                softly.assertThat(result.category()).isEqualTo(category.getDisplayName());
                softly.assertThat(result.lowestPrices()).isEqualTo(lowestPricesDto);
                softly.assertThat(result.highestPrices()).isEqualTo(highestPricesDto);
            });
        }

        @Test
        void 존재하지_않는_카테고리로_조회_하는_경우_실패() {
            String invalidCategory = "BOTTOM";
            assertThatThrownBy(() -> productService.findPriceRangeCategory(invalidCategory))
                    .isInstanceOf(InvalidCategoryException.class)
                    .hasMessageContaining(ErrorCode.CATEGORY_NOT_FOUND.getMessage() + invalidCategory);
        }
    }


}
