package dev.ehyeon.auth.sign.controller;

import dev.ehyeon.auth.sign.controller.request.SignInRequest;
import dev.ehyeon.auth.sign.controller.request.SignUpRequest;
import dev.ehyeon.auth.sign.service.SignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SignController {

    private final SignService signService;

    @PostMapping("/sign-in")
    public ResponseEntity<Void> signIn(HttpServletRequest httpServletRequest, @Valid @RequestBody SignInRequest signInRequest) {
        Long userId = signService.signIn(signInRequest.username(), signInRequest.password());

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("userId", userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        signService.signUp(request.username(), request.password());

        return ResponseEntity.ok().build();
    }
}
