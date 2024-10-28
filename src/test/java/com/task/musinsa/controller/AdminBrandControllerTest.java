package com.task.musinsa.controller;

import com.task.musinsa.dto.BrandResponseDto;
import com.task.musinsa.dto.CreateBrandRequestDto;
import com.task.musinsa.exception.DuplicateNameException;
import com.task.musinsa.exception.ErrorCode;
import com.task.musinsa.service.BrandService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminBrandController.class)
class AdminBrandControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BrandService brandService;

    @Test
    void 브랜드_생성_성공() throws Exception {
        var requestDto = new CreateBrandRequestDto("무신사 스탠다드");
        var responseDto = BrandResponseDto.builder().id(1L).build();

        given(brandService.createBrand(any(CreateBrandRequestDto.class))).willReturn(responseDto);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"brandName\":\"무신사 스탠다드\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("브랜드 생성 성공"))
                .andExpect(jsonPath("$.data.id").value(responseDto.id()));

        verify(brandService).createBrand(any(CreateBrandRequestDto.class));
    }

    @Test
    void 중복된_브랜드_이름으로_생성_실패() throws Exception {
        // Given
        var requestDto = new CreateBrandRequestDto("A");

        when(brandService.createBrand(any(CreateBrandRequestDto.class)))
                .thenThrow(new DuplicateNameException("브랜드가 이미 존재합니다: " + requestDto.brandName()));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"brandName\":\"A\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_NAME.getMessage()));

        verify(brandService).createBrand(any(CreateBrandRequestDto.class));
    }

}
