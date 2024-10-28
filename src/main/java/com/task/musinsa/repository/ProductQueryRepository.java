package com.task.musinsa.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.task.musinsa.domain.Category;
import com.task.musinsa.domain.QProduct;
import com.task.musinsa.dto.BrandTotalPriceDto;
import com.task.musinsa.dto.CategoryLowestPriceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.task.musinsa.dto.ProductPriceResponseDto.BrandPrice;
import static com.task.musinsa.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 최저가 브랜드와 총액 조회
     */
    public Optional<BrandTotalPriceDto> findLowestPriceByBrand() {
        QProduct productSub = new QProduct("productSub");

        return Optional.ofNullable(
                queryFactory
                        .select(
                                Projections.constructor(
                                        BrandTotalPriceDto.class,
                                        product.brand.id,
                                        product.brand.name,
                                        product.price.sum()
                                )
                        )
                        .from(product)
                        .where(product.price.eq(
                                JPAExpressions.select(productSub.price.min())
                                        .from(productSub)
                                        .where(productSub.category.eq(product.category)
                                                .and(productSub.brand.eq(product.brand)))
                        ))
                        .groupBy(product.brand)
                        .orderBy(product.price.sum().asc())
                        .orderBy(product.brand.name.asc())
                        .limit(1)
                        .fetchOne()
        );
    }

    /**
     * 브랜드의 카테고리별 최저가 상품 조회
     */
    public List<CategoryLowestPriceDto> findLowestProductByBrand(Long brandId) {
        QProduct productSub = new QProduct("productSub");
        return queryFactory
                .select(
                        Projections.constructor(
                                CategoryLowestPriceDto.class,
                                product.category,
                                product.price
                        )
                )
                .from(product)
                .where(product.brand.id.eq(brandId)
                        .and(product.price.eq(
                                JPAExpressions.select(productSub.price.min())
                                        .from(productSub)
                                        .where(productSub.category.eq(product.category)
                                                .and(productSub.brand.id.eq(brandId)))
                        )))
                .groupBy(product.category)
                .fetch();
    }

    public BrandPrice findLowestPriceByCategory(Category category) {
        QProduct productSub = new QProduct("productSub");
        return queryFactory
                .select(Projections.constructor(
                        BrandPrice.class,
                        product.brand.name,
                        product.price
                ))
                .from(product)
                .where(product.category.eq(category)
                        .and(product.price.eq(
                                JPAExpressions.select(productSub.price.min())
                                        .from(productSub)
                                        .where(productSub.category.eq(category))
                        )))
                .fetchOne();
    }

    public BrandPrice findHighestPriceByCategory(Category category) {
        QProduct productSub = new QProduct("productSub");
        return queryFactory
                .select(Projections.constructor(
                        BrandPrice.class,
                        product.brand.name,
                        product.price
                ))
                .from(product)
                .where(product.category.eq(category)
                        .and(product.price.eq(
                                JPAExpressions.select(productSub.price.max())
                                        .from(productSub)
                                        .where(productSub.category.eq(category))
                        )))
                .fetchOne();
    }
}
