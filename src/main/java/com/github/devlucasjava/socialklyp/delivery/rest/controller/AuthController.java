package com.github.devlucasjava.socialklyp.delivery.rest.controller;

import com.github.devlucasjava.socialklyp.application.dto.request.auth.LoginDTO;
import com.github.devlucasjava.socialklyp.application.dto.request.auth.RegisterDTO;
import com.github.devlucasjava.socialklyp.application.dto.request.auth.UpdatePasswordDTO;
import com.github.devlucasjava.socialklyp.application.dto.request.auth.VerifyPasswordDTO;
import com.github.devlucasjava.socialklyp.application.dto.response.auth.JwtAuthDTO;
import com.github.devlucasjava.socialklyp.application.dto.response.utils.BooleanDTO;
import com.github.devlucasjava.socialklyp.application.service.AuthService;
import com.github.devlucasjava.socialklyp.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<JwtAuthDTO> register(
            @Valid @RequestBody RegisterDTO registerDTO) {

        return ResponseEntity.ok(authService.register(registerDTO));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<JwtAuthDTO> login(
            @Valid @RequestBody LoginDTO loginDTO) {

        return ResponseEntity.ok(authService.login(loginDTO));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token using refresh token")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<JwtAuthDTO> refreshToken(
            @AuthenticationPrincipal User user
    ) {
        JwtAuthDTO response = authService.refreshToken(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-password")
    @Operation(summary = "Verify user password")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BooleanDTO> verifyPassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody VerifyPasswordDTO dto) {

        return ResponseEntity.ok(authService.verifyPassword(user, dto));
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update user password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdatePasswordDTO dto) {

        authService.updatePassword(user, dto);
        return ResponseEntity.noContent().build();
    }
}