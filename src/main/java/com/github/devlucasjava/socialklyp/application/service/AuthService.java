package com.github.devlucasjava.socialklyp.application.service;

import com.github.devlucasjava.socialklyp.application.dto.request.auth.LoginDTO;
import com.github.devlucasjava.socialklyp.application.dto.request.auth.RegisterDTO;
import com.github.devlucasjava.socialklyp.application.dto.request.auth.UpdatePasswordDTO;
import com.github.devlucasjava.socialklyp.application.dto.request.auth.VerifyPasswordDTO;
import com.github.devlucasjava.socialklyp.application.dto.response.auth.JwtAuthDTO;
import com.github.devlucasjava.socialklyp.application.dto.response.utils.BooleanDTO;
import com.github.devlucasjava.socialklyp.application.mapper.UserMapper;
import com.github.devlucasjava.socialklyp.delivery.rest.advice.ConflictException;
import com.github.devlucasjava.socialklyp.delivery.rest.advice.InvalidCredentialsException;
import com.github.devlucasjava.socialklyp.delivery.rest.advice.InvalidOrExpiredTokenException;
import com.github.devlucasjava.socialklyp.delivery.rest.advice.ResourceNotFoundException;
import com.github.devlucasjava.socialklyp.domain.entity.Profile;
import com.github.devlucasjava.socialklyp.domain.entity.User;
import com.github.devlucasjava.socialklyp.domain.enuns.Role;
import com.github.devlucasjava.socialklyp.infrastructure.database.repository.UserRepository;
import com.github.devlucasjava.socialklyp.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtAuthDTO register(RegisterDTO request) {

        if (userRepository.existsByEmail(request.getEmail())){
            throw new ConflictException("This is email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())){
            throw new ConflictException("This is username already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Set.of(Role.USER));

        Profile profile = new Profile();
        profile.setDisplayName(request.getUsername());
        profile.setBio("Hello i am new user in Social KLYP");
        profile.setPrivate(false);
        profile.setUser(user);

        user.setProfile(profile);
        userRepository.save(user);

        return JwtAuthDTO.builder()
                .user(userMapper.toDTO(user))
                .build();
    }


    public JwtAuthDTO login(LoginDTO request) {
        User user = userRepository.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(InvalidCredentialsException::new);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        final String accessToken = jwtService.generateAccessToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);

        return JwtAuthDTO.builder()
                .user(userMapper.toDTO(user))
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.extractExpiration(accessToken))
                .refreshExpiresIn(jwtService.extractExpiration(refreshToken))
                .build();
    }

    public JwtAuthDTO refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!jwtService.isValidToken(refreshToken, user)) {
            throw new InvalidOrExpiredTokenException();
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return JwtAuthDTO.builder()
                .user(userMapper.toDTO(user))
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtService.extractExpiration(newAccessToken))
                .refreshExpiresIn(jwtService.extractExpiration(newRefreshToken))
                .build();
    }


    @Transactional
    public void updatePassword(User userRequest, UpdatePasswordDTO request) {

        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String hash = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(hash);

        userRepository.save(user);
    }

    public BooleanDTO verifyPassword(User userRequest, VerifyPasswordDTO request) {

        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return BooleanDTO.builder()
                    .valid(false)
                    .build();
        }

        return BooleanDTO.builder()
                .valid(true)
                .build();
    }
}
