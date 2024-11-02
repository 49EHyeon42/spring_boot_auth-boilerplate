package dev.ehyeon.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession httpSession = request.getSession(false);

        if (httpSession == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // userId is Long
        Object userId = httpSession.getAttribute("userId");

        if (userId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        CustomAuthenticationToken customAuthenticationToken = new CustomAuthenticationToken(userId);

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(customAuthenticationToken);
        } catch (AuthenticationException exception) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!authentication.isAuthenticated()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
