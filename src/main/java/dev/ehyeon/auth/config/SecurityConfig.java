package dev.ehyeon.auth.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final HandlerMappingIntrospector introspector;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
        httpSecurity
                // 필요 시 cors 설정
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers(mvcRequestMatcher(HttpMethod.POST, "/api/v1/sign-in")).permitAll()
                                .requestMatchers(mvcRequestMatcher(HttpMethod.POST, "/api/v1/sign-up")).permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .logout(httpSecurityLogoutConfigurer ->
                        httpSecurityLogoutConfigurer
                                .clearAuthentication(true)
                                .invalidateHttpSession(true)
                                .logoutRequestMatcher(mvcRequestMatcher(HttpMethod.POST, "/api/v1/sign-out"))
                                .permitAll()
                                .deleteCookies("JSESSIONID")
                                .logoutSuccessHandler((request, response, authentication) ->
                                        response.setStatus(HttpServletResponse.SC_OK)))
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(new SessionAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    private MvcRequestMatcher mvcRequestMatcher(HttpMethod httpMethod, String pattern) {
        MvcRequestMatcher mvcRequestMatcher = new MvcRequestMatcher(introspector, pattern);
        mvcRequestMatcher.setMethod(httpMethod);
        return mvcRequestMatcher;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(new CustomAuthenticationProvider(customUserDetailsService));
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
