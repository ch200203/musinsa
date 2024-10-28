package com.task.musinsa.mapper;

import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.CategoryLowestPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static com.task.musinsa.dto.ProductPriceResponseDto.CategoryPrice;
import static com.task.musinsa.dto.ProductPriceResponseDto.LowestCategoryPrice;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "category", expression = "java(product.getCategory().getDisplayName())")
    @Mapping(target = "brand", source = "brand.name")
    @Mapping(target = "price", source = "price")
    LowestCategoryPrice toLowestCategoryPrice(Product product);

    CategoryPrice toCategoryPrice(CategoryLowestPriceDto categoryLowestPriceDto);
}


