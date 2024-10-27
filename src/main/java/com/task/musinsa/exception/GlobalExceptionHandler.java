package com.task.musinsa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<CustomErrorResponse> handleDuplicateNameException(DuplicateNameException ex) {
        var errorResponse = CustomErrorResponse.error(ErrorCode.DUPLICATE_NAME);
        return ResponseEntity.status(ErrorCode.DUPLICATE_NAME.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<CustomErrorResponse> handleInvalidCategoryException(InvalidCategoryException ex) {
        var errorResponse = CustomErrorResponse.error(ErrorCode.INVALID_CATEGORY);
        return ResponseEntity.status(ErrorCode.INVALID_CATEGORY.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        var errorResponse = CustomErrorResponse.error(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleException(Exception ex) {
        CustomErrorResponse errorResponse = CustomErrorResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
