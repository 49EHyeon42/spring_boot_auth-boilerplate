package dev.ehyeon.auth.config;

import dev.ehyeon.auth.user.exception.NotFoundUserException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtRolesService JwtRolesService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        long memberId;

        try {
            memberId = jwtProvider.getMemberIdFromToken(authorizationHeader);
        } catch (InvalidTokenException exception) {
            filterChain.doFilter(request, response);
            return;
        }

        Collection<? extends GrantedAuthority> roles;

        try {
            roles = JwtRolesService.findRolesByUserId(memberId);
        } catch (NotFoundUserException exception) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(memberId, roles));

        filterChain.doFilter(request, response);
    }
}
