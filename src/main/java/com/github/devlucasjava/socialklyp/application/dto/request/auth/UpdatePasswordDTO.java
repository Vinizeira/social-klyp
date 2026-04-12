package com.github.devlucasjava.socialklyp.application.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Data transfer object for updating user password")
public class UpdatePasswordDTO {

    @NotBlank(message = "Current password is required")
    @Schema(example = "!12345678")
    private String currentPassword;

    @NotBlank(message = "New Password is required")
    @Size(min = 8, max = 50, message = "the password must be between 8 and 50 characters")
    @Schema(example = "!123456789")
    private String newPassword;
}