package com.task.musinsa.service;

import com.task.musinsa.domain.Brand;
import com.task.musinsa.domain.Category;
import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.CreateProductRequestDto;
import com.task.musinsa.dto.ProductResponseDto;
import com.task.musinsa.dto.UpdateProductRequestDto;
import com.task.musinsa.exception.CustomException;
import com.task.musinsa.exception.ErrorCode;
import com.task.musinsa.repository.BrandRepository;
import com.task.musinsa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    @Transactional
    public ProductResponseDto createProduct(final CreateProductRequestDto request) {
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new CustomException(ErrorCode.BRAND_NOT_FOUND));

        Category category = request.category();
        BigDecimal price = request.price();

        Product product = Product.create(brand, category, request.productName(), price);
        Product result = productRepository.save(product);

        return ProductResponseDto.builder()
                .id(result.getId())
                .build();
    }

    @Transactional
    public void updateProduct(long productId, UpdateProductRequestDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        updateProductDetails(product, request);
    }


    private void updateProductDetails(Product product, UpdateProductRequestDto request) {
        updateProductName(product, request.newName());
        updateProductPrice(product, request.newPrice());
    }

    private void updateProductName(Product product, String newName) {
        if (newName != null && !newName.isBlank()) {
            product.changeName(newName);
        }
    }

    private void updateProductPrice(Product product, BigDecimal newPrice) {
        if (newPrice != null && newPrice.compareTo(BigDecimal.ZERO) > 0) {
            product.changePrice(newPrice);
        }
    }

    @Transactional
    public void deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    }


}
