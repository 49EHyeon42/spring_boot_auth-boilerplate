package dev.ehyeon.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ehyeon.auth.sign.controller.request.SignInRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SessionSignInFilter extends OncePerRequestFilter {

    private final RequestMatcher requestMatcher;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!requiresAuthentication(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        SignInRequest signInRequest = getSignInRequest(request);

        CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(signInRequest.username(), signInRequest.password());

        try {
            Authentication authentication = authenticationManager.authenticate(customAuthenticationToken);

            if (authentication.isAuthenticated()) {
                HttpSession session = request.getSession(true);

                session.setAttribute("userId", Long.parseLong(authentication.getPrincipal().toString()));
                session.setAttribute("userAuthorities", authentication.getAuthorities());

                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (AuthenticationException exception) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        return requestMatcher.matches(request);
    }

    private SignInRequest getSignInRequest(HttpServletRequest request) throws IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        return objectMapper.readValue(requestBody, SignInRequest.class);
    }
}
