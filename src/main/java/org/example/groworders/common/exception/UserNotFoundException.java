package org.example.groworders.common.exception;

import org.example.groworders.common.model.BaseResponseStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String message) {
        super(BaseResponseStatus.INVALID_USER_INFO, message);
    }
}