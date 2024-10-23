package dev.ehyeon.auth.user.exception;

import dev.ehyeon.auth.base.BaseException;
import dev.ehyeon.auth.global.ErrorCode;

public class UsernameAlreadyExistsException extends BaseException {

    public UsernameAlreadyExistsException() {
        super(ErrorCode.USERNAME_ALREADY_EXISTS.getMessage(), ErrorCode.USERNAME_ALREADY_EXISTS.getHttpStatus());
    }
}
