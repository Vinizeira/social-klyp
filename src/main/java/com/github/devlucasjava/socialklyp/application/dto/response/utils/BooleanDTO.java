package com.github.devlucasjava.socialklyp.application.dto.response.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Generic boolean response")
public class BooleanDTO {

    @Schema(description = "Result of validation", example = "true")
    private boolean valid;
}