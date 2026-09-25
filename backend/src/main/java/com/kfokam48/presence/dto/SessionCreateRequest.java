package com.kfokam48.presence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SessionCreateRequest(
        @NotBlank(message = "le titre est obligatoire") String titre,
        @NotNull(message = "la promotion est obligatoire") Long promotionId
) {
}
