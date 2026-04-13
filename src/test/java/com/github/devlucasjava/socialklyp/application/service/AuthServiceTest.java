package com.github.devlucasjava.socialklyp.application.service;

import com.github.devlucasjava.socialklyp.application.dto.request.auth.*;
import com.github.devlucasjava.socialklyp.application.dto.response.auth.JwtAuthDTO;
import com.github.devlucasjava.socialklyp.application.dto.response.utils.BooleanDTO;
import com.github.devlucasjava.socialklyp.application.mapper.UserMapper;
import com.github.devlucasjava.socialklyp.domain.entity.User;
import com.github.devlucasjava.socialklyp.infrastructure.database.repository.UserRepository;
import com.github.devlucasjava.socialklyp.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(uuid);
        user.setUsername("lucas");
        user.setEmail("lucas@test.com");
        user.setPassword("encoded-pass");
        user.setRoles(Set.of());
    }

    // ---------------- REGISTER ----------------

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("lucas");
        dto.setEmail("lucas@test.com");
        dto.setPassword("123456");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);

        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded-pass");

        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        when(jwtService.getExpirationToken("access-token")).thenReturn(Instant.now());
        when(jwtService.getExpirationToken("refresh-token")).thenReturn(Instant.now());

        when(userMapper.toDTO(user)).thenReturn(null);

        JwtAuthDTO response = authService.register(dto);

        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    // ---------------- LOGIN ----------------

    @Test
    void shouldLoginSuccessfully() {

        LoginDTO dto = new LoginDTO();
        dto.setLogin("lucas");
        dto.setPassword("123456");

        when(userRepository.findByUsernameOrEmail(dto.getLogin()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                .thenReturn(true);

        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        when(jwtService.getExpirationToken("access-token")).thenReturn(Instant.now());
        when(jwtService.getExpirationToken("refresh-token")).thenReturn(Instant.now());

        when(userMapper.toDTO(user)).thenReturn(null);

        JwtAuthDTO response = authService.login(dto);

        assertEquals("access-token", response.getToken());
    }

    // ---------------- REFRESH TOKEN ----------------

    @Test
    void shouldRefreshTokenSuccessfully() {

        when(userRepository.findByUsernameOrEmail("lucas"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateAccessToken(user)).thenReturn("new-access");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh");

        when(jwtService.getExpirationToken("new-access")).thenReturn(Instant.now());
        when(jwtService.getExpirationToken("new-refresh")).thenReturn(Instant.now());

        when(userMapper.toDTO(user)).thenReturn(null);

        JwtAuthDTO response = authService.refreshToken(user);

        assertEquals("new-access", response.getToken());
        assertEquals("new-refresh", response.getRefreshToken());
    }

    // ---------------- UPDATE PASSWORD ----------------

    @Test
    void shouldUpdatePasswordSuccessfully() {

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("654321")).thenReturn("new-encoded");

        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setCurrentPassword("123456");
        dto.setNewPassword("654321");

        authService.updatePassword(user, dto);

        verify(userRepository).save(user);
    }

    // ---------------- VERIFY PASSWORD ----------------

    @Test
    void shouldReturnTrueWhenPasswordIsValid() {

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", user.getPassword())).thenReturn(true);

        VerifyPasswordDTO dto = new VerifyPasswordDTO();
        dto.setPassword("123456");

        BooleanDTO response = authService.verifyPassword(user, dto);

        assertTrue(response.isValid());
    }

    @Test
    void shouldReturnFalseWhenPasswordIsInvalid() {

        when(userRepository.findById(uuid)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        VerifyPasswordDTO dto = new VerifyPasswordDTO();
        dto.setPassword("wrong");

        BooleanDTO response = authService.verifyPassword(user, dto);

        assertFalse(response.isValid());
    }
}