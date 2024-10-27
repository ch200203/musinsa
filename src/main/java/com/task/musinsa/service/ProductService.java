package com.task.musinsa.service;

import com.task.musinsa.domain.Brand;
import com.task.musinsa.domain.Category;
import com.task.musinsa.domain.Product;
import com.task.musinsa.dto.CreateProductRequestDto;
import com.task.musinsa.dto.UpdateProductRequestDto;
import com.task.musinsa.exception.NotFoundException;
import com.task.musinsa.repository.BrandRepository;
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

    @Transactional
    public Product createProduct(final CreateProductRequestDto request) {
        Brand brand = brandRepository.findById(request.brandId())
                .orElseThrow(() -> new NotFoundException("브랜드를 찾을 수 없습니다: " + request.brandId()));

        Category category = request.category();
        BigDecimal price = request.price();

        Product product = Product.create(brand, category, request.productName(), price);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(long productId, UpdateProductRequestDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다: " + productId));

        updateProductDetails(product, request);

        return product;
    }

    @Transactional
    public void deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다: " + productId));

        productRepository.delete(product);
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
}
