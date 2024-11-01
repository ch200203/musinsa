package com.task.musinsa.service;

import com.task.musinsa.exception.CustomException;
import com.task.musinsa.exception.ErrorCode;
import com.task.musinsa.exception.MissingCategoryPricesException;
import com.task.musinsa.mapper.ProductMapper;
import com.task.musinsa.domain.Category;
import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.CategoryLowestPriceDto;
import com.task.musinsa.repository.ProductQueryRepository;
import com.task.musinsa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.task.musinsa.dto.ProductPriceResponseDto.*;
import static com.task.musinsa.dto.ProductPriceResponseDto.TotalPriceResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerProductService {

    private final ProductQueryRepository productQueryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * 카테고리별 최저가와 총액을 찾아 반환
     */
    public TotalPriceResponse findLowestPriceByCategory() {
        List<Product> products = productRepository.findAll();

        // 1. 카테고리별 최저가 상품 반환
        Map<String, Product> lowestPriceProducts = findLowestPriceByCategory(products);

        // 2. 총 가격 계산
        BigDecimal totalPrice = calculateTotalPrice(lowestPriceProducts);

        // 3. DTO 로 변환하여 반환
        var lowestPrices = lowestPriceProducts.entrySet().stream()
                .map(entry -> {
                    Product product = entry.getValue();
                    return (product == null) ?
                            defaultCategoryPriceInfo(entry.getKey()) : productMapper.toLowestCategoryPrice(product);
                })
                .toList();

        return TotalPriceResponse.builder()
                .lowestPrices(lowestPrices)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * 카테고리별 최저가 상품 목록 조회
     */
    private Map<String, Product> findLowestPriceByCategory(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(
                        it -> it.getCategory().getDisplayName(),
                        Collectors.collectingAndThen(
                                Collectors.minBy((Comparator.comparing(Product::getPrice))),
                                opt -> opt.orElse(null) // 상품이 없는 경우 Null
                        )
                ));
    }

    /**
     * 총 가격 계산
     */
    private BigDecimal calculateTotalPrice(Map<String, Product> lowestPriceProducts) {
        return lowestPriceProducts.values().stream()
                .filter(Objects::nonNull) // null 상품이 존재하지 않는 경우는 제외하고 계산.
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 카테고리에 해당하는 브랜드가 없는 기본 CategoryPriceInfo 생성
     */
    private LowestCategoryPrice defaultCategoryPriceInfo(String category) {
        return new LowestCategoryPrice(category, "", BigDecimal.ZERO);
    }

    /**
     * 브랜드의 카테고리별 최저가
     */
    public BrandCategoryPrice findLowestPriceByBrand() {
        // 1. 카테고리별 합계 최저가 브랜드 조회
        var lowestPriceByBrand = productQueryRepository.findLowestPriceByBrand()
                .orElseThrow(() -> new CustomException(ErrorCode.BRAND_NOT_FOUND));

        // 2. 카테고리별 최저가 상품 조회
        var lowestProductsByBrand = productQueryRepository.findLowestProductByBrand(lowestPriceByBrand.brandId());

        // 3. 카테고리별 상품이 모두 있는지 확인
        validAllCategories(lowestProductsByBrand);

        // 4. 카테고리별 최저가 상품을 DTO로 변환하여 응답 생성
        var categoryPrices = lowestProductsByBrand.stream()
                .map(productMapper::toCategoryPrice)
                .toList();

        return BrandCategoryPrice.builder()
                .brand(lowestPriceByBrand.brandName())
                .lowestCategoryPrices(categoryPrices)
                .totalPrice(lowestPriceByBrand.totalPrice())
                .build();
    }

    /**
     * 모든 카테고리가 있는지 확인하는 메서드
     */
    private void validAllCategories(List<CategoryLowestPriceDto> products) {
        if (products.isEmpty() || products.stream()
                .map(CategoryLowestPriceDto::category)
                .distinct()
                .count() < Category.values().length) {
            throw new MissingCategoryPricesException();
        }
    }

    public CategoryPriceRangeDto findPriceRangeCategory(String categoryName) {
        Category category = Category.fromDisplayName(categoryName);

        // 1. 카테고리 별 최저가 브랜드 조회
        var lowestPriceByCategory = productQueryRepository.findLowestPriceByCategory(category);

        // 2. 최고가 브랜드 조회
        var highestPriceByCategory = productQueryRepository.findHighestPriceByCategory(category);

        // 3. dto 변환
        var lowestPrices = List.of(lowestPriceByCategory);
        var highestPrices = List.of(highestPriceByCategory);

        return CategoryPriceRangeDto.builder()
                .category(category.getDisplayName())
                .lowestPrices(lowestPrices)
                .highestPrices(highestPrices)
                .build();
    }
}
