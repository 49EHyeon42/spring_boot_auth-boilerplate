package dev.ehyeon.auth.global;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Getter
public enum ErrorCode {

    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "NOT_FOUND_USER"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USERNAME_ALREADY_EXISTS");

    private final HttpStatus httpStatus;
    private final String message;
}
