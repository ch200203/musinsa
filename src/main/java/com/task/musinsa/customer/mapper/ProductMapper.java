package com.task.musinsa.customer.mapper;

import com.task.musinsa.customer.dto.ProductPriceResponseDto;
import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.CategoryLowestPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "category", expression = "java(product.getCategory().getDisplayName())")
    @Mapping(target = "brand", source = "brand.name")
    @Mapping(target = "price", source = "price")
    ProductPriceResponseDto.LowestCategoryPrice toLowestCategoryPrice(Product product);

    ProductPriceResponseDto.CategoryPrice toCategoryPrice(CategoryLowestPriceDto categoryLowestPriceDto);

}


