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

    @Transactional
    public JwtAuthDTO register(RegisterDTO request) {

        if (userRepository.existsByEmail(request.getEmail())){
            throw new ConflictException("This is email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())){
            throw new ConflictException("This is username already exists");
        }

        User user = userMapper.toEntity(request);
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.USER));

        Profile profile = new Profile();
        profile.setDisplayName(request.getUsername());
        profile.setBio("Hello i am new user in Social KLYP");
        profile.setPrivate(false);
        profile.setUser(user);

        user.setProfile(profile);
        User userSaved = userRepository.saveAndFlush(user);

        String accessToken = jwtService.generateAccessToken(userSaved);
        String refreshToken = jwtService.generateRefreshToken(userSaved);

        return JwtAuthDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationToken(accessToken))
                .refreshExpiresIn(jwtService.getExpirationToken(refreshToken))
                .user(userMapper.toDTO(userSaved))
                .build();
    }


    public JwtAuthDTO login(LoginDTO request) {

        User user = userRepository.findByUsernameOrEmail(request.getLogin())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        final String accessToken = jwtService.generateAccessToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);

        return JwtAuthDTO.builder()
                .user(userMapper.toDTO(user))
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationToken(accessToken))
                .refreshExpiresIn(jwtService.getExpirationToken(refreshToken))
                .build();
    }

    public JwtAuthDTO refreshToken(User request) {
        User user = userRepository.findByUsernameOrEmail(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return JwtAuthDTO.builder()
                .user(userMapper.toDTO(user))
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtService.getExpirationToken(newAccessToken))
                .refreshExpiresIn(jwtService.getExpirationToken(newRefreshToken))
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
