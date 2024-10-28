package com.task.musinsa.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.musinsa.domain.Brand;
import com.task.musinsa.domain.Category;
import com.task.musinsa.dto.CreateProductRequestDto;
import com.task.musinsa.dto.ProductResponseDto;
import com.task.musinsa.dto.UpdateProductRequestDto;
import com.task.musinsa.exception.CustomException;
import com.task.musinsa.exception.ErrorCode;
import com.task.musinsa.repository.BrandRepository;
import com.task.musinsa.service.ProductService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminProductController.class)
class AdminProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProductService productService;

    @MockBean
    BrandRepository brandRepository;

    @Nested
    class 상품_생성_테스트 {
        @Test
        void 상품_생성을_요청하여_성공한다() throws Exception {
            // given
            var request = new CreateProductRequestDto(
                    1L,
                    Category.TOP,
                    "옥스포드 셔츠",
                    BigDecimal.valueOf(10_000)
            );

            var response = ProductResponseDto.builder().id(1L).build();


            given(productService.createProduct(any(CreateProductRequestDto.class))).willReturn(response);
            given(brandRepository.findById(anyLong())).willReturn(Optional.of(Brand.of("A")));

            mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/admin/product")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.message").value("상품 생성 성공"))
                    .andExpect(jsonPath("$.data.id").value(response.id()));

            verify(productService).createProduct(any(CreateProductRequestDto.class));
        }


        @Test
        void 상품_생성_요청_잘못된_요청_값으로_실패() throws Exception {
            // given
            var invalidRequest = new CreateProductRequestDto(
                    null,  // 브랜드 ID가 null임
                    Category.TOP,
                    "",    // 빈 상품명
                    null   // 가격이 null임
            );

            // when & then
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/admin/product")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("잘못된 요청 값이 전달되었습니다.")) // 에러 메시지가 검증 메시지와 일치하는지 확인
                    .andExpect(jsonPath("$.data.brandId").value("브랜드 ID를 반드시 입력하여야 합니다."))
                    .andExpect(jsonPath("$.data.productName").value("상품명을 반드시 입력하여야 합니다."))
                    .andExpect(jsonPath("$.data.price").value("가격을 반드시 입력하여야 합니다."));
        }

        @Test
        void 상품_생성_요청_존재하지_않는_브랜드_실패() throws Exception {
            // given
            var validCreateRequest = new CreateProductRequestDto(
                    999L, // 존재하지 않는 브랜드 ID
                    Category.TOP,
                    "테스트 상품",
                    BigDecimal.valueOf(10000)
            );

            given(productService.createProduct(any(CreateProductRequestDto.class)))
                    .willThrow(new CustomException(ErrorCode.BRAND_NOT_FOUND));

            // when & then
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/api/admin/product")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(validCreateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("해당 브랜드를 찾을 수 없습니다."));
        }
    }


    @Nested
    class 상품_변경_테스트 {

        @Test
        void 상품_업데이트_요청하여_성공() throws Exception {
            // given
            long productId = 1L;

            var request = new UpdateProductRequestDto(
                    "플란넬 셔츠",
                    BigDecimal.valueOf(15_000)
            );

            doNothing().when(productService).updateProduct(anyLong(), any(UpdateProductRequestDto.class));

            // when & then
            mockMvc.perform(
                            MockMvcRequestBuilders.patch("/api/admin/product/{productId}", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("상품 업데이트 성공"));
        }

        @Test
        void 상품_업데이트_요청_존재하지_않는_ID_실패() throws Exception {
            // given
            var request = new UpdateProductRequestDto(
                    "새로운 상품명",
                    BigDecimal.valueOf(15_000)
            );

            doThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                    .when(productService).updateProduct(anyLong(), any(UpdateProductRequestDto.class));

            // when & then
            mockMvc.perform(
                            MockMvcRequestBuilders.patch("/api/admin/product/{productId}", 999L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("해당 상품을 찾을 수 없습니다."));
        }

        @Test
        void 상품_업데이트_요청_잘못된_요청_값으로_실패() throws Exception {
            // given
            var invalidRequest = new UpdateProductRequestDto(
                    "",
                    BigDecimal.ZERO
            );

            mockMvc.perform(
                            MockMvcRequestBuilders.patch("/api/admin/product/{productId}", 1L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("잘못된 요청 값이 전달되었습니다."))
                    .andExpect(jsonPath("$.data.newName").value("상품명을 반드시 입력하여야 합니다."))
                    .andExpect(jsonPath("$.data.newPrice").value("가격은 0보다 커야 합니다."));
        }

    }

    @Nested
    class 상품_삭제_테스트 {
        @Test
        void 상품_삭제_요청_성공() throws Exception {
            doNothing().when(productService).deleteProduct(anyLong());

            mockMvc.perform(
                            MockMvcRequestBuilders.delete("/api/admin/product/{productId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("상품 삭제 성공"));
        }

        @Test
        void 상품_삭제_요청_존재하지_않는_ID_실패() throws Exception {
            // given
            doThrow(new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                    .when(productService).deleteProduct(anyLong());

            mockMvc.perform(
                            MockMvcRequestBuilders.delete("/api/admin/product/{productId}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.statusCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("해당 상품을 찾을 수 없습니다."));
        }
    }
}
