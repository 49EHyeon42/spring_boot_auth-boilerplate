package dev.ehyeon.auth.sign.controller.request;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
