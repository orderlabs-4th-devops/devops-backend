package org.example.groworders.common.exception;

import org.example.groworders.common.model.BaseResponseStatus;

public class EmailSendException extends BaseException {
    public EmailSendException(String message, Throwable cause) {
        super(BaseResponseStatus.INVALID_USER_EMAIL, message, cause);
    }
}