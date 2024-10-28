package com.task.musinsa.exception;

import static com.task.musinsa.exception.ErrorCode.MISSING_CATEGORY_PRICES;

public class MissingCategoryPricesException extends CustomException {
    public MissingCategoryPricesException() {
        super(MISSING_CATEGORY_PRICES);
    }
}
