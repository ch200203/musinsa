package com.task.musinsa.customer.mapper;

import com.task.musinsa.customer.dto.ProductPriceResponseDto;
import com.task.musinsa.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductPriceResponseDto.CategoryPriceInfo toCategoryPriceInfo(Product product);

}

