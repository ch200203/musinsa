package com.task.musinsa.exception;

import com.task.musinsa.common.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<CustomErrorResponse>> handleNotFoundException(NotFoundException ex) {
        var errorResponse = CustomErrorResponse.error(ErrorCode.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.fail(errorResponse));
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ApiResponse<CustomErrorResponse>> handleDuplicateNameException(DuplicateNameException ex) {
        var errorResponse = CustomErrorResponse.error(ErrorCode.DUPLICATE_NAME);
        return ResponseEntity.status(ErrorCode.DUPLICATE_NAME.getStatus()).body(ApiResponse.fail(errorResponse));
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<ApiResponse<CustomErrorResponse>> handleInvalidCategoryException(InvalidCategoryException ex) {
        var errorResponse = CustomErrorResponse.error(ErrorCode.INVALID_CATEGORY);
        return ResponseEntity.status(ErrorCode.INVALID_CATEGORY.getStatus()).body(ApiResponse.fail(errorResponse));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<CustomErrorResponse>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        var errorResponse = CustomErrorResponse.error(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.fail(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<CustomErrorResponse>> handleException(Exception ex) {
        CustomErrorResponse errorResponse = CustomErrorResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.fail(errorResponse));
    }
}
