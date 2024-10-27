package com.task.musinsa.customer.service;

import com.task.musinsa.customer.dto.ProductPriceResponseDto;
import com.task.musinsa.customer.mapper.ProductMapper;
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

import static com.task.musinsa.customer.dto.ProductPriceResponseDto.TotalPriceResponse;

@Service
@RequiredArgsConstructor
public class CustomerProductService {

    private final ProductQueryRepository productQueryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /**
     * 카테고리별 최저가와 총액을 찾아 반환
     */
    @Transactional
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

        return new TotalPriceResponse(lowestPrices, totalPrice);
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
     *
     * @param lowestPriceProducts
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
    private ProductPriceResponseDto.LowestCategoryPrice defaultCategoryPriceInfo(String category) {
        return new ProductPriceResponseDto.LowestCategoryPrice(category, "", BigDecimal.ZERO);
    }

    /**
     * 브랜드의 카테고리별 최저가
     */
    public ProductPriceResponseDto.BrandCategoryPrice findLowestPriceByBrand() {
        List<Product> products = productRepository.findAll();

        // 1. 브랜드들의 카테고리별 최저가 상품 목록 조회
        var lowestPriceByBrand = productQueryRepository.findLowestPriceByBrand()
                .orElseThrow(() -> new IllegalArgumentException("최저가 브랜드를 찾을 수 없습니다."));

        // 2. 카테고리별 최저가 상품 조회
        var lowestProductsByBrand = productQueryRepository.findLowestProductByBrand(lowestPriceByBrand.brandId());

        // 3. 카테고리별 상품이 모두 있는지 확인
        if (!hasAllCategories(lowestProductsByBrand)) {
            throw new IllegalArgumentException("해당 브랜드에서 모든 카테고리의 최저가 상품을 찾을 수 없습니다.");
        }

        // 4. 카테고리별 최저가 상품을 DTO로 변환하여 응답 생성
        var categoryPrices = lowestProductsByBrand.stream()
                .map(productMapper::toCategoryPrice)
                .toList();

        return new ProductPriceResponseDto.BrandCategoryPrice(
                lowestPriceByBrand.brandName(),
                categoryPrices,
                lowestPriceByBrand.totalPrice()
        );
    }

    private Map<String, List<Product>> getBrandCategoryPrices(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(
                        product -> product.getBrand().getName()
                ));
    }

    /**
     * 모든 카테고리가 있는지 확인하는 메서드
     */
    private boolean hasAllCategories(List<CategoryLowestPriceDto> products) {
        long categoryCount = products.stream()
                .map(CategoryLowestPriceDto::category)
                .distinct()
                .count();
        return categoryCount == Category.values().length;
    }
}
