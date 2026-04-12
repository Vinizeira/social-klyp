package com.github.devlucasjava.socialklyp.application.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "User registration data")
public class RegisterDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must have at least 2 and max 50 characters")
    @Schema(example = "Lucas")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must have at least 2 and max 50 characters")
    @Schema(example = "Silva")
    private String lastName;

    @NotNull(message = "Data birth day is required")
    @Past(message = "Data birth day must be in the past")
    @Schema(example = "2000-01-01")
    private LocalDate birthDay;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must have at least 3 and max 30 characters")
    @Schema(example = "lucasdev123")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email not valid")
    @Schema(example = "lucas@klyp.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must have at least 8 and max 30 characters")
    @Schema(example = "!12345678")
    private String password;
}