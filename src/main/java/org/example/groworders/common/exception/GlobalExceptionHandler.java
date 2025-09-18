package org.example.groworders.common.exception;

import org.example.groworders.common.model.BaseResponse;
import org.example.groworders.common.model.BaseResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 BaseException 처리~~
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {
        BaseResponse<?> response = BaseResponse.fail(
                e.getStatus().getCode(),
                e.getMessage() != null ? e.getMessage() : e.getStatus().getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // DTO 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        BaseResponse<?> response = BaseResponse.fail(
                BaseResponseStatus.METHOD_ARGUMENT_NOT_VALID.getCode(),
                errorMessage.toString()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        e.printStackTrace();

        BaseResponse<?> response = BaseResponse.fail(
                BaseResponseStatus.SERVER_ERROR.getCode(),
                BaseResponseStatus.SERVER_ERROR.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
