package com.github.devlucasjava.socialklyp.infrastructure.security;

import com.github.devlucasjava.socialklyp.delivery.rest.advice.InvalidCredentialsException;
import com.github.devlucasjava.socialklyp.domain.entity.User;
import com.github.devlucasjava.socialklyp.infrastructure.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(InvalidCredentialsException::new);
    }
}
