package com.task.musinsa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.musinsa.exception.CustomException;
import com.task.musinsa.exception.ErrorCode;
import com.task.musinsa.exception.MissingCategoryPricesException;
import com.task.musinsa.service.CustomerProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static com.task.musinsa.dto.ProductPriceResponseDto.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerProductController.class)
class CustomerProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private CustomerProductService customerProductService;

    @Test
    void 카테고리별_최저가_조회_API_테스트() throws Exception {
        // given
        var lowestPrices = List.of(
                new LowestCategoryPrice("상의", "C", BigDecimal.valueOf(10000)),
                new LowestCategoryPrice("아우터", "E", BigDecimal.valueOf(5000)),
                new LowestCategoryPrice("바지", "D", BigDecimal.valueOf(3000)),
                new LowestCategoryPrice("스니커즈", "G", BigDecimal.valueOf(9000)),
                new LowestCategoryPrice("가방", "A", BigDecimal.valueOf(2000)),
                new LowestCategoryPrice("모자", "D", BigDecimal.valueOf(1500)),
                new LowestCategoryPrice("양말", "I", BigDecimal.valueOf(1700)),
                new LowestCategoryPrice("액세서리", "F", BigDecimal.valueOf(1900))
        );
        var totalPrice = BigDecimal.valueOf(34_100);
        var response = TotalPriceResponse.builder()
                .lowestPrices(lowestPrices)
                .totalPrice(totalPrice)
                .build();

        given(customerProductService.findLowestPriceByCategory()).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/customer/products/lowest-price-by-category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value("34,100"))
                .andExpect(jsonPath("$.lowestPrices").isArray())
                .andExpect(jsonPath("$.lowestPrices[0].category").value("상의"))
                .andExpect(jsonPath("$.lowestPrices[0].brand").value("C"))
                .andExpect(jsonPath("$.lowestPrices[0].price").value("10,000"))
                .andExpect(jsonPath("$.lowestPrices[1].category").value("아우터"))
                .andExpect(jsonPath("$.lowestPrices[1].brand").value("E"))
                .andExpect(jsonPath("$.lowestPrices[1].price").value("5,000"))
                .andExpect(jsonPath("$.lowestPrices[2].category").value("바지"))
                .andExpect(jsonPath("$.lowestPrices[2].brand").value("D"))
                .andExpect(jsonPath("$.lowestPrices[2].price").value("3,000"))
                .andExpect(jsonPath("$.lowestPrices[3].category").value("스니커즈"))
                .andExpect(jsonPath("$.lowestPrices[3].brand").value("G"))
                .andExpect(jsonPath("$.lowestPrices[3].price").value("9,000"))
                .andExpect(jsonPath("$.lowestPrices[4].category").value("가방"))
                .andExpect(jsonPath("$.lowestPrices[4].brand").value("A"))
                .andExpect(jsonPath("$.lowestPrices[4].price").value("2,000"))
                .andExpect(jsonPath("$.lowestPrices[5].category").value("모자"))
                .andExpect(jsonPath("$.lowestPrices[5].brand").value("D"))
                .andExpect(jsonPath("$.lowestPrices[5].price").value("1,500"))
                .andExpect(jsonPath("$.lowestPrices[6].category").value("양말"))
                .andExpect(jsonPath("$.lowestPrices[6].brand").value("I"))
                .andExpect(jsonPath("$.lowestPrices[6].price").value("1,700"))
                .andExpect(jsonPath("$.lowestPrices[7].category").value("액세서리"))
                .andExpect(jsonPath("$.lowestPrices[7].brand").value("F"))
                .andExpect(jsonPath("$.lowestPrices[7].price").value("1,900"));
    }

    @Test
    void 카테고리_상품을_구매할_때_최저가격에_판매하는_브랜드와_상품정보를_조회() throws Exception {
        // given
        List<CategoryPrice> categoryPrices = List.of(
                new CategoryPrice("상의", BigDecimal.valueOf(10100)),
                new CategoryPrice("아우터", BigDecimal.valueOf(5100)),
                new CategoryPrice("바지", BigDecimal.valueOf(3000)),
                new CategoryPrice("스니커즈", BigDecimal.valueOf(9500)),
                new CategoryPrice("가방", BigDecimal.valueOf(2500)),
                new CategoryPrice("모자", BigDecimal.valueOf(1500)),
                new CategoryPrice("양말", BigDecimal.valueOf(2400)),
                new CategoryPrice("액세서리", BigDecimal.valueOf(2000))
        );
        BigDecimal totalAmount = BigDecimal.valueOf(36100);
        var response = new BrandCategoryPrice("D", categoryPrices, totalAmount);

        given(customerProductService.findLowestPriceByBrand()).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/customer/products/lowest-price-by-brand")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("D"))
                .andExpect(jsonPath("$.lowestCategoryPrices").isArray())
                .andExpect(jsonPath("$.lowestCategoryPrices[0].category").value("상의"))
                .andExpect(jsonPath("$.lowestCategoryPrices[0].price").value("10,100"))
                .andExpect(jsonPath("$.lowestCategoryPrices[1].category").value("아우터"))
                .andExpect(jsonPath("$.lowestCategoryPrices[1].price").value("5,100"))
                .andExpect(jsonPath("$.lowestCategoryPrices[2].category").value("바지"))
                .andExpect(jsonPath("$.lowestCategoryPrices[2].price").value("3,000"))
                .andExpect(jsonPath("$.lowestCategoryPrices[3].category").value("스니커즈"))
                .andExpect(jsonPath("$.lowestCategoryPrices[3].price").value("9,500"))
                .andExpect(jsonPath("$.lowestCategoryPrices[4].category").value("가방"))
                .andExpect(jsonPath("$.lowestCategoryPrices[4].price").value("2,500"))
                .andExpect(jsonPath("$.lowestCategoryPrices[5].category").value("모자"))
                .andExpect(jsonPath("$.lowestCategoryPrices[5].price").value("1,500"))
                .andExpect(jsonPath("$.lowestCategoryPrices[6].category").value("양말"))
                .andExpect(jsonPath("$.lowestCategoryPrices[6].price").value("2,400"))
                .andExpect(jsonPath("$.lowestCategoryPrices[7].category").value("액세서리"))
                .andExpect(jsonPath("$.lowestCategoryPrices[7].price").value("2,000"))
                .andExpect(jsonPath("$.totalPrice").value("36,100"));
    }

    @Test
    void 모든_카테고리에_최저가_상품이_없는_경우_예외_발생() throws Exception {
        // given
        given(customerProductService.findLowestPriceByBrand()).willThrow(new MissingCategoryPricesException());

        // when & then
        mockMvc.perform(get("/api/customer/products/lowest-price-by-brand")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.MISSING_CATEGORY_PRICES.getStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.MISSING_CATEGORY_PRICES.getMessage()));
    }

    @Test
    void 카테고리_최고_최저_가격_조회() throws Exception {
        String categoryName = "상의";
        var lowestPrice = new BrandPrice("C", BigDecimal.valueOf(10_000));
        var highestPrice = new BrandPrice("I", BigDecimal.valueOf(11_400));
        var response = new CategoryPriceRangeDto(categoryName, List.of(lowestPrice), List.of(highestPrice));

        given(customerProductService.findPriceRangeCategory(categoryName)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/customer/products/price-info/{categoryName}", categoryName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("상의"))
                .andExpect(jsonPath("$.lowestPrices[0].brand").value("C"))
                .andExpect(jsonPath("$.lowestPrices[0].price").value("10,000"))
                .andExpect(jsonPath("$.highestPrices[0].brand").value("I"))
                .andExpect(jsonPath("$.highestPrices[0].price").value("11,400"));
    }

    @Test
    void 잘못된_카테고리_이름으로_조회() throws Exception {
        // given
        String invalidCategoryName = "BOTTOM";
        given(customerProductService.findPriceRangeCategory(invalidCategoryName))
                .willThrow(new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/customer/products/price-info/{categoryName}", invalidCategoryName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(ErrorCode.CATEGORY_NOT_FOUND.getStatus()))
                .andExpect(jsonPath("$.message").value(ErrorCode.CATEGORY_NOT_FOUND.getMessage()));
    }

    @Test
    void 동일_최저가_브랜드가_여러개인_경우_테스트() throws Exception {
        // given
        String categoryName = "모자";

        List<BrandPrice> lowestPrices = List.of(
                new BrandPrice("A", BigDecimal.valueOf(1_700)),
                new BrandPrice("G", BigDecimal.valueOf(1_700))
        );
        List<BrandPrice> highestPrices = List.of(
                new BrandPrice("B", BigDecimal.valueOf(2_000))
        );
        var response = new CategoryPriceRangeDto(categoryName, lowestPrices, highestPrices);

        given(customerProductService.findPriceRangeCategory(categoryName)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/customer/products/price-info/{categoryName}", "모자")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("모자"))
                .andExpect(jsonPath("$.lowestPrices[0].brand").value("A"))
                .andExpect(jsonPath("$.lowestPrices[0].price").value("1,700"))
                .andExpect(jsonPath("$.lowestPrices[1].brand").value("G"))
                .andExpect(jsonPath("$.lowestPrices[1].price").value("1,700"))
                .andExpect(jsonPath("$.highestPrices[0].brand").value("B"))
                .andExpect(jsonPath("$.highestPrices[0].price").value("2,000"));
    }
}
