package dev.ehyeon.auth.config;

import dev.ehyeon.auth.base.BaseException;
import dev.ehyeon.auth.global.ErrorCode;

public class InvalidTokenException extends BaseException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN.getMessage(), ErrorCode.INVALID_TOKEN.getHttpStatus());
    }
}
