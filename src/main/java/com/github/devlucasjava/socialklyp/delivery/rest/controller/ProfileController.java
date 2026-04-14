package com.github.devlucasjava.socialklyp.delivery.rest.controller;

import com.github.devlucasjava.socialklyp.application.dto.response.user.ProfileDTO;
import com.github.devlucasjava.socialklyp.application.service.ProfileService;
import com.github.devlucasjava.socialklyp.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
@Tag(name = "Profiles", description = "Profile endpoints")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "Get authenticated user profile")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProfileDTO> getMyProfile(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(profileService.getMyProfile(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get profile by ID")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProfileDTO> getProfileById(
            @PathVariable UUID id) {

        return ResponseEntity.ok(profileService.getProfileById(id));
    }
}
