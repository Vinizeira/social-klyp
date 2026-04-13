package com.github.devlucasjava.socialklyp.application.dto.response.user;

import com.github.devlucasjava.socialklyp.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Schema(description = "User response data")
public class UserDTO {

    @Schema(description = "User unique ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "First name", example = "Lucas")
    private String firstName;

    @Schema(description = "Last name", example = "Silva")
    private String lastName;

    @Schema(description = "Birth date", example = "2000-01-01")
    private LocalDate birthDay;

    @Schema(description = "Unique username", example = "lucasdev123")
    private String username;

    @Schema(description = "User email", example = "lucas@email.com")
    private String email;

    @Schema(description = "User roles", example = "[\"USER\"]")
    private Set<Role> roles;

    @Schema(description = "Account creation date", example = "2026-01-01T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Account update date", example = "2026-01-01T10:15:30")
    private LocalDateTime updatedAt;
}