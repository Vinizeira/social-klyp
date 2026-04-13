package com.github.devlucasjava.socialklyp.infrastructure.config;

import com.github.devlucasjava.socialklyp.domain.entity.User;
import com.github.devlucasjava.socialklyp.domain.enums.Role;
import com.github.devlucasjava.socialklyp.infrastructure.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class InitUserConfig  implements CommandLineRunner {

    private final InitUserProperties properties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {

        if (!properties.isEnabled()) {
            return;
        }

        boolean exists = userRepository.existsByUsername(properties.getUsername());
        if (exists) {
            return;
        }

        User user = new User();
        user.setFirstName("Lucas");
        user.setLastName("Admin");
        user.setEmail(properties.getEmail());
        user.setBirthDay(LocalDate.now());
        user.setUsername(properties.getUsername());
        user.setPassword(passwordEncoder.encode(properties.getPassword()));
        user.setRoles(Set.of(Role.ADMIN, Role.USER));
        userRepository.save(user);

    }
}
