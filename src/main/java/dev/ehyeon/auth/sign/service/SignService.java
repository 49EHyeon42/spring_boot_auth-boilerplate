package dev.ehyeon.auth.sign.service;

import dev.ehyeon.auth.user.entity.UserEntity;
import dev.ehyeon.auth.user.entity.UserRole;
import dev.ehyeon.auth.user.exception.NotFoundUserException;
import dev.ehyeon.auth.user.exception.UsernameAlreadyExistsException;
import dev.ehyeon.auth.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class SignService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long signIn(String username, String password) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(NotFoundUserException::new);

        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new NotFoundUserException();
        }

        return userEntity.getId();
    }

    public void signUp(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(password);

        UserEntity userEntity = new UserEntity(username, encodedPassword, UserRole.ROLE_USER);

        userRepository.save(userEntity);
    }
}
