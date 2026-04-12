package com.github.devlucasjava.socialklyp.application.dto.response.auth;

import com.github.devlucasjava.socialklyp.application.dto.response.user.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@Schema(description = "Authentication response")
public class JwtAuthDTO {

    @Schema(example = "Bearer")
    private String typeToken = "Bearer";

    @Schema(description = "JWT access token")
    private String token;

    @Schema(description = "JWT refresh token")
    private String refreshToken;

    @Schema(description = "Access token expiration")
    private Instant expiresIn;

    @Schema(description = "Refresh token expiration")
    private Instant refreshExpiresIn;

    @Schema(description = "Authenticated user")
    private UserDTO user;
}