package org.example.groworders.common.exception;

import org.example.groworders.common.model.BaseResponseStatus;

public class EmailVerifyException extends BaseException {
    public EmailVerifyException(String message) {
        super(BaseResponseStatus.EMAIL_VERIFY_FAIL, message);
    }
}
