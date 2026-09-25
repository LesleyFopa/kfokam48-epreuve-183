package com.kfokam48.presence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciceRequest(
        @NotNull(message = "la session est obligatoire") Long sessionId,
        @NotNull(message = "l'étudiant est obligatoire") Long etudiantId,
        @NotBlank(message = "le lien est obligatoire") String lien
) {
}
