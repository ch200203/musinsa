package com.task.musinsa.service;

import com.task.musinsa.domain.Brand;
import com.task.musinsa.domain.Category;
import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.CreateProductRequestDto;
import com.task.musinsa.dto.ProductResponseDto;
import com.task.musinsa.dto.UpdateProductRequestDto;
import com.task.musinsa.exception.CustomException;
import com.task.musinsa.exception.ErrorCode;
import com.task.musinsa.repository.BrandRepository;
import com.task.musinsa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private ProductService productService;

    private Brand brand;
    private Category category;
    private Product product;
    private CreateProductRequestDto createRequest;

    @Nested
    class 상품_생성_테스트 {

        private Brand brand;
        private CreateProductRequestDto createRequest;

        @BeforeEach
        void setUp() {
            brand = Brand.of("무신사 스탠다드");
            createRequest = new CreateProductRequestDto(
                    1L, Category.TOP, "옥스포드 셔츠", BigDecimal.valueOf(12_000)
            );
        }

        @Test
        void 상품_추가에_성공한다() {
            // given
            when(brandRepository.findById(createRequest.brandId())).thenReturn(Optional.of(brand));
            when(productRepository.save(any(Product.class))).thenAnswer(it -> {
                Product savedProduct = it.getArgument(0);
                ReflectionTestUtils.setField(savedProduct, "id", 1L);
                return savedProduct;
            });

            // when
            ProductResponseDto result = productService.createProduct(createRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(result).isNotNull();
                softly.assertThat(result.id()).isEqualTo(1L);
            });
        }
    }

    @Nested
    class 상품_변경_테스트 {
        private Product product;
        private UpdateProductRequestDto updateRequest;

        @BeforeEach
        void setUp() {
            Brand brand = Brand.of("무신사 스탠다드");
            product = Product.create(brand, Category.TOP, "옥스포드 셔츠", BigDecimal.valueOf(12000));
            ReflectionTestUtils.setField(product, "id", 1L);

            updateRequest = new UpdateProductRequestDto(
                    "드레스 셔츠", BigDecimal.valueOf(15_000)
            );
        }

        @Test
        void 상품_변경_성공() {
            // given
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

            // when
            productService.updateProduct(1L, updateRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(product.getName()).isEqualTo("드레스 셔츠");
                softly.assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(15_000));
            });

            verify(productRepository).findById(1L);
        }

        @Test
        void 상품_변경에_실패_상품_없음() {
            // given
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.updateProduct(1L, updateRequest))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class 상품_삭제_테스트 {
        private Product product;

        @BeforeEach
        void setUp() {
            Brand brand = Brand.of("무신사 스탠다드");
            product = Product.create(brand, Category.TOP, "옥스포드 셔츠", BigDecimal.valueOf(12000));
            ReflectionTestUtils.setField(product, "id", 1L);
        }

        @Test
        void 상품_삭제에_성공한다() {
            // given
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

            // when
            productService.deleteProduct(1L);

            // then
            verify(productRepository, times(1)).delete(product);
        }

        @Test
        void 상품_삭제에_실패한다_상품_없음() {
            // given
            when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.deleteProduct(1L))
                    .isInstanceOf(CustomException.class)
                    .hasMessageContaining(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

}
