package com.task.musinsa.service;

import com.task.musinsa.domain.Brand;
import com.task.musinsa.domain.Category;
import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.CreateProductRequestDto;
import com.task.musinsa.dto.UpdateProductRequestDto;
import com.task.musinsa.exception.NotFoundException;
import com.task.musinsa.repository.BrandRepository;
import com.task.musinsa.repository.CategoryRepository;
import com.task.musinsa.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Brand brand;
    private Category category;
    private Product product;
    private CreateProductRequestDto createRequest;

    @BeforeEach
    void setUp() {
        brand = Brand.of("무신사 스탠다드");
        category = Category.of("상의");
        product = Product.create(brand, category, "옥스포드 셔츠", BigDecimal.valueOf(12000));
        ReflectionTestUtils.setField(product, "id", 1L);

        createRequest = new CreateProductRequestDto(
                1L, 1L, "옥스포드 셔츠", 12000
        );
    }

    @Test
    void 상품_추가에_성공한다() {
        // given
        when(brandRepository.findById(createRequest.brandId())).thenReturn(Optional.of(brand));
        when(categoryRepository.findById(createRequest.categoryId())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(it -> {
            Product savedProduct = it.getArgument(0);
            ReflectionTestUtils.setField(savedProduct, "id", 1L);
            return savedProduct;
        });

        // when
        Product createdProduct = productService.createProduct(createRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(createdProduct).isNotNull();
            softly.assertThat(createdProduct.getId()).isEqualTo(1L);
            softly.assertThat(createdProduct.getBrand()).isEqualTo(brand);
            softly.assertThat(createdProduct.getCategory()).isEqualTo(category);
            softly.assertThat(createdProduct.getName()).isEqualTo("옥스포드 셔츠");
            softly.assertThat(createdProduct.getPrice()).isEqualByComparingTo(new BigDecimal("12000"));
        });
    }


    @Test
    void 상품_수정에_성공한다() {
        // given
        UpdateProductRequestDto updateRequest = new UpdateProductRequestDto(
                "플란넬 셔츠", new BigDecimal("15000")
        );
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        Product updatedProduct = productService.updateProduct(1L, updateRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(updatedProduct.getName()).isEqualTo("플란넬 셔츠");
            softly.assertThat(updatedProduct.getPrice()).isEqualByComparingTo(new BigDecimal("15000"));
        });
    }

    @Test
    void 존재하지_않는_상품_수정_시_실패한다() {
        // given
        UpdateProductRequestDto updateRequest = new UpdateProductRequestDto(
                "업그레이드 셔츠", new BigDecimal("15000")
        );
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.updateProduct(1L, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }

    @Test
    void 상품_삭제에_성공한다() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        productService.deleteProduct(1L);

        // then
        verify(productRepository).delete(product);
    }

    @Test
    void 존재하지_않는_상품_삭제_시_실패한다() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }

}
