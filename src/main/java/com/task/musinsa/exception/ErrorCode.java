package com.task.musinsa.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    CATEGORY_NOT_FOUND("해당 카테고리를 찾을 수 없습니다.", 404),
    BRAND_NOT_FOUND("해당 브랜드를 찾을 수 없습니다.", 404),
    MISSING_CATEGORY_PRICES("모든 카테고리에 해당하는 상품이 없습니다.", 404),
    INTERNAL_SERVER_ERROR("서버 오류입니다.", 500),
    DUPLICATE_NAME("중복된 이름이 있습니다.", 409),
    INVALID_CATEGORY("잘못된 카테고리 값입니다.", 400);

    private final String message;
    private final int status;
}
