package org.example.groworders.common.exception;

import lombok.Getter;
import org.example.groworders.common.model.BaseResponseStatus;

@Getter
public class BaseException extends RuntimeException{

    private final BaseResponseStatus status;

    public BaseException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public BaseException(BaseResponseStatus status, String message) {
        super(message);
        this.status = status;
    }

    public BaseException(BaseResponseStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public static BaseException from(BaseResponseStatus status) {
        return new BaseException(status, status.getMessage());
    }
}
