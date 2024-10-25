package com.task.musinsa.service;

import com.task.musinsa.domain.Brand;
import com.task.musinsa.domain.Category;
import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.CreateProductRequestDto;
import com.task.musinsa.dto.UpdateProductRequestDto;
import com.task.musinsa.exception.NotFoundException;
import com.task.musinsa.repository.BrandRepository;
import com.task.musinsa.repository.CategoryRepository;
import com.task.musinsa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Product createProduct(final CreateProductRequestDto request) {
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new NotFoundException("브랜드를 찾을 수 없습니다: " + request.brandId()));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다: " + request.categoryId()));

        Product product = Product.create(brand, category, request.productName(), BigDecimal.valueOf(request.price()));
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long productId, UpdateProductRequestDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다: " + productId));

        if (request.newName() != null && !request.newName().isBlank()) {
            product.changeName(request.newName());
        }

        if (request.newPrice() != null && request.newPrice().compareTo(BigDecimal.ZERO) > 0) {
            product.changePrice(request.newPrice());
        }

        return product;
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다: " + productId));

        productRepository.delete(product);
    }
}
