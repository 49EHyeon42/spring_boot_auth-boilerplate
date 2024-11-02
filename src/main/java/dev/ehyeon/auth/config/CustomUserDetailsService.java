package dev.ehyeon.auth.config;

import dev.ehyeon.auth.global.ErrorCode;
import dev.ehyeon.auth.user.entity.UserEntity;
import dev.ehyeon.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userId = Long.parseLong(username);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USERNAME_NOT_FOUND.getMessage()));

        return new CustomUserDetails(userEntity);
    }
}
