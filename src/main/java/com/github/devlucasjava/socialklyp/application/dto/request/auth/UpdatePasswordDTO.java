package com.github.devlucasjava.socialklyp.application.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordDTO {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New Password is required")
    @Size(min = 8, max = 50, message = "the password must be between 8 and 50 characters")
    private String newPassword;
}