package dev.ehyeon.auth.config;

import dev.ehyeon.auth.user.entity.UserEntity;
import dev.ehyeon.auth.user.exception.NotFoundUserException;
import dev.ehyeon.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtRolesService {

    private final UserRepository userRepository;

    public Collection<? extends GrantedAuthority> findRolesByUserId(long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(NotFoundUserException::new);

        return Set.of(new SimpleGrantedAuthority(userEntity.getRole().name()));
    }
}
