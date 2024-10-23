package dev.ehyeon.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class SessionAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession httpSession = request.getSession(false);

        if (httpSession != null) {
            Long userId = (Long) httpSession.getAttribute("userId");

            // fixme - 권한을 어떻게 처리하는 것이 좋을까?
            SecurityContextHolder.getContext().setAuthentication(new CustomAuthenticationToken(userId, null));
        }

        filterChain.doFilter(request, response);
    }
}
