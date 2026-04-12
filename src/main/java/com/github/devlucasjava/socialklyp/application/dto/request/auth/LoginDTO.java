package com.github.devlucasjava.socialklyp.application.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Login data")
public class LoginDTO {
    @NotBlank(message = "Login is required, username or email")
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters")
    @Schema(example = "lucasdev123")
    private String login;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")

    @Schema(example = "!12345678")
    private String password;
}
