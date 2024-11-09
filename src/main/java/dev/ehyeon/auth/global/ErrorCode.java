package dev.ehyeon.auth.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Spring Security Error Code
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN"),
    USERNAME_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USERNAME_NOT_FOUND"),

    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "NOT_FOUND_USER"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS");

    private final HttpStatus httpStatus;
    private final String message;
}
