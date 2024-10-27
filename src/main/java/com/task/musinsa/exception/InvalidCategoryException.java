package com.task.musinsa.exception;

public class InvalidCategoryException extends RuntimeException {

    public InvalidCategoryException(String category) {
        super("잘못된 카테고리 값입니다: " + category);
    }
}
