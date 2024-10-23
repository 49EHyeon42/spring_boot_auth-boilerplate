package dev.ehyeon.auth.user.exception;

import dev.ehyeon.auth.base.BaseException;
import dev.ehyeon.auth.global.ErrorCode;

public class NotFoundUserException extends BaseException {

    public NotFoundUserException() {
        super(ErrorCode.NOT_FOUND_USER.getMessage(), ErrorCode.NOT_FOUND_USER.getHttpStatus());
    }
}
