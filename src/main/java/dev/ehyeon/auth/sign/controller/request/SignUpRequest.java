package dev.ehyeon.auth.sign.controller.request;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
