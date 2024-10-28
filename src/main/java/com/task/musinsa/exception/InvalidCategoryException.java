package com.task.musinsa.exception;

public class InvalidCategoryException extends RuntimeException {

    public InvalidCategoryException(String category) {
        super(ErrorCode.CATEGORY_NOT_FOUND.getMessage() + category);
    }
}
