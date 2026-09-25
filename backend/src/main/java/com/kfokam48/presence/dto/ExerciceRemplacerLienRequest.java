package com.kfokam48.presence.dto;

import jakarta.validation.constraints.NotBlank;

public record ExerciceRemplacerLienRequest(
        @NotBlank(message = "le lien est obligatoire") String lien
) {
}
