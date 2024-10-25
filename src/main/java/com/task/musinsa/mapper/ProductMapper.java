package com.task.musinsa.mapper;

import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.CategoryPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "product.category.displayName", target = "category")
    @Mapping(source = "product.brand.name", target = "brand")
    CategoryPriceDto toCategoryPriceInfoDto(Product product);
}
