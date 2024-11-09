package dev.ehyeon.auth.sign.controller;

import dev.ehyeon.auth.config.JwtProvider;
import dev.ehyeon.auth.sign.controller.request.SignInRequest;
import dev.ehyeon.auth.sign.controller.request.SignUpRequest;
import dev.ehyeon.auth.sign.service.SignService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;
    private final JwtProvider jwtProvider;

    @PostMapping("/sign-in")
    public ResponseEntity<Void> signIn(@Valid @RequestBody SignInRequest signInRequest, HttpServletResponse response) {
        Long userId = signService.signIn(signInRequest.username(), signInRequest.password());

        String accessToken = jwtProvider.generateAccessToken(userId);

        response.setHeader("Authorization", accessToken);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        signService.signUp(request.username(), request.password());

        return ResponseEntity.ok().build();
    }
}
