package com.task.musinsa.customer.service;

import com.task.musinsa.customer.mapper.ProductMapper;
import com.task.musinsa.domain.Product;
import com.task.musinsa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.task.musinsa.customer.dto.ProductPriceResponseDto.CategoryPriceInfo;
import static com.task.musinsa.customer.dto.ProductPriceResponseDto.TotalPriceResponse;

@Service
@RequiredArgsConstructor
public class CustomerProductService {

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
        List<CategoryPriceInfo> lowestPrices = lowestPriceProducts.entrySet().stream()
                .map(entry -> {
                    Product product = entry.getValue();
                    return (product == null) ?
                            defaultCategoryPriceInfo(entry.getKey()) : productMapper.toCategoryPriceInfo(product);
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
                                // opt -> opt.orElseThrow(() -> new IllegalArgumentException("카테고리에 해당하는 상품이 없습니다."))
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
                .filter(Objects::nonNull) // null 상품이 존재하지 않는 경우는ㄴ 제외하고 계산.
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 카테고리에 해당하는 브랜드가 없는 기본 CategoryPriceInfo 생성
     */
    private CategoryPriceInfo defaultCategoryPriceInfo(String category) {
        return new CategoryPriceInfo(category, "", BigDecimal.ZERO);
    }
}
