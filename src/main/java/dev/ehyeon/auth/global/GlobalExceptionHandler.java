package dev.ehyeon.auth.global;

import dev.ehyeon.auth.base.BaseException;
import dev.ehyeon.auth.global.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException exception) {
        return ResponseEntity.status(exception.getHttpStatus()).body(new ErrorResponse(exception.getMessage()));
    }
}
