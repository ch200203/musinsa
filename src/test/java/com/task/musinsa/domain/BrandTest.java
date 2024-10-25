package com.task.musinsa.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BrandTest {

    @Test
    void 브랜드_객체_생성에_성공한다() {
        Brand brand = Brand.of("무신사");
        assertThat(brand.getName()).isEqualTo("무신사");
    }

    @Test
    void 브랜드명_이름이_50자를_초과할_때_예외가_발생한다() {
        String longName = "a".repeat(51); // 51자 이름 생성
        assertThatThrownBy(() -> Brand.of(longName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("브랜드명은 50자를 넘을 수 없습니다.");
    }

    @Test
    void 브랜드명_이름이_비어있을_때_예외가_발생한다() {
        assertThatThrownBy(() -> Brand.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("브랜드명은 필수입니다.");

        assertThatThrownBy(() -> Brand.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("브랜드명은 필수입니다.");
    }

}
