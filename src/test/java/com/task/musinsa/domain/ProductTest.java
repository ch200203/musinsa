package com.task.musinsa.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


class ProductTest {

    private Brand brand;
    private Category category;
    private BigDecimal price;
    private String productName;

    @BeforeEach
    void setUp() {
        brand = Brand.of("무신사");
        category = Category.TOP;
        productName = "옥스포드 셔츠";
        price = new BigDecimal(12_000);
    }

    @Test
    void 상품_생성에_성공한다() {
        // when
        Product product = Product.create(brand, category, productName, price);

        // then
        assertSoftly(it -> {
            it.assertThat(product).isNotNull();
            it.assertThat(product.getCategory()).isEqualTo(category);
            it.assertThat(product.getBrand()).isEqualTo(brand);
            it.assertThat(product.getName()).isEqualTo(productName);
            it.assertThat(product.getPrice()).isEqualByComparingTo(price);
        });
    }

    @Test
    void 상품명_비어있을_때_생성에_실패한다() {
        // when & then
        assertThatThrownBy(() -> Product.create(brand, category, "", price))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품명은 필수입니다.");
    }

    @Test
    void 상품명_길이_초과_시_생성에_실패한다() {
        String longName = "a".repeat(101); // 101자 상품명 생성

        // when & then
        assertThatThrownBy(() -> Product.create(brand, category, longName, price))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품명은 100자를 넘을 수 없습니다.");
    }

    @Test
    void 상품_가격_변경에_성공한다() {
        Product product = Product.create(brand, category, productName, price);
        BigDecimal newPrice = new BigDecimal(15_000);

        // when
        product.changePrice(newPrice);

        // then
        assertThat(product.getPrice()).isEqualByComparingTo(newPrice);
    }

    @Test
    void 상품_이름_변경에_성공한다() {
        Product product = Product.create(brand, category, productName, price);
        String newName = "드레스 셔츠";

        // when
        product.changeName(newName);

        // then
        assertThat(product.getName()).isEqualTo(newName);
    }

    @Test
    void 상품_가격_변경시_0이하의_금액으로_변경에_실패한다() {
        Product product = Product.create(brand, category, productName, price);

        // when & then
        assertThatThrownBy(() -> product.changePrice(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 가격은 0보다 커야 합니다.");

        assertThatThrownBy(() -> product.changePrice(new BigDecimal(-5_000)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 가격은 0보다 커야 합니다.");
    }
}
