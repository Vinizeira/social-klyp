package com.github.devlucasjava.socialklyp.application.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "Profile response data")
public class ProfileDTO {

    @Schema(description = "Profile ID")
    private UUID id;

    @Schema(description = "Display name", example = "Vn Dev")
    private String displayName;

    @Schema(description = "Bio", example = "Backend developer")
    private String bio;

    @Schema(description = "Profile picture URL")
    private String profilepictureUrl;

    @Schema(description = "Private account")
    private boolean isPrivate;

    @Schema(description = "Username")
    private String username;
}
