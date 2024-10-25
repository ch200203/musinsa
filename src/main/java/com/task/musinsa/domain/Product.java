package com.task.musinsa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"brand_id", "category_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    private Product(Brand brand, Category category, String name, BigDecimal price) {
        this.brand = brand;
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public static Product create(Brand brand, Category category, String name, BigDecimal price) {
        validateName(name);
        validatePrice(price);
        return new Product(brand, category, name, price);
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("상품명은 100자를 넘을 수 없습니다.");
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }
    }

    public void changePrice(BigDecimal newPrice) {
        validatePrice(newPrice);
        this.price = newPrice;
    }

    public void changeName(String newName) {
        validateName(newName);
        this.name = newName;
    }
}
