package com.task.musinsa.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomErrorResponse {
    private String errorCode;
    private String message;

    public static CustomErrorResponse error(ErrorCode errorCode) {
        return new CustomErrorResponse(errorCode.name(), errorCode.getMessage());
    }
}
