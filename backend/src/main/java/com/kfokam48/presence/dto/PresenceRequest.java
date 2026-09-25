package com.kfokam48.presence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PresenceRequest(
        @NotBlank(message = "le code est obligatoire") String code,
        @NotNull(message = "l'étudiant est obligatoire") Long etudiantId
) {
}
