package com.task.musinsa.exception;

import com.task.musinsa.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ApiResponse> handleDuplicateNameException(DuplicateNameException ex) {
        var errorResponse = ApiResponse.fail(ErrorCode.DUPLICATE_NAME);
        return ResponseEntity.status(ErrorCode.DUPLICATE_NAME.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<ApiResponse> handleInvalidCategoryException(InvalidCategoryException ex) {
        var errorResponse = ApiResponse.fail(ErrorCode.INVALID_CATEGORY);
        return ResponseEntity.status(ErrorCode.INVALID_CATEGORY.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.fail(ex.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        ApiResponse errorResponse = ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
