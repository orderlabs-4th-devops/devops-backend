package org.example.groworders.common.exception;

import org.example.groworders.common.model.BaseResponseStatus;

public class FileUploadException extends BaseException {

    public FileUploadException(String message) {
        super(BaseResponseStatus.FILE_UPLOAD_ERROR, message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(BaseResponseStatus.PROFILE_IMAGE_UPLOAD_ERROR, message, cause);
    }

}
