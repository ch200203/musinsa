package com.task.musinsa.repository;

import com.task.musinsa.config.QueryDslConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


@DataJpaTest
@Import({ProductQueryRepository.class, QueryDslConfiguration.class})
class ProductQueryRepositoryTest {

    @Autowired
    private ProductQueryRepository productQueryRepository;

    @Autowired
    private ProductRepository productRepository;


    @Test
    void 브랜드별_카테고리_최저가_상품_조회하여_반환한다() {
        var result = productQueryRepository.findLowestPriceByBrand();

        assertThat(result).isNotEmpty();
        assertThat(result.get().brandName()).isEqualTo("D"); // init TestData 기반의 값
        assertThat(result.get().totalPrice().compareTo(new BigDecimal("36100"))).isEqualTo(0);
    }

    @Test
    void 최저가_브랜드가_없을_경우_빈_값_반환() {
        // 모든 데이터를 삭제
        productRepository.deleteAll();

        // when
        var result = productQueryRepository.findLowestPriceByBrand();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void 브랜드의_카테고리별_최저가_상품을_조회한다() {
        long brandId = 4;
        Map<String, BigDecimal> expected = Map.of(
                "상의", new BigDecimal(10_100),
                "아우터", new BigDecimal(5_100),
                "바지", new BigDecimal(3_000),
                "스니커즈", new BigDecimal(9_500),
                "가방", new BigDecimal(2_500),
                "모자", new BigDecimal(1_500),
                "양말", new BigDecimal(2_400),
                "액세서리", new BigDecimal(2_000)
        );

        var results = productQueryRepository.findLowestProductByBrand(brandId);

        assertSoftly(softly -> {
            softly.assertThat(results).isNotEmpty();

            results.forEach(it -> {
                String category = it.category().getDisplayName();
                BigDecimal price = expected.get(category);
                System.out.println("Category: " + category + ", price: " + price);
                softly.assertThat(it.price().compareTo(price)).isEqualTo(0);
            });
        });

    }
}
