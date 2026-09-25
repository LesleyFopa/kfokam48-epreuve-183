package com.kfokam48.presence.dto;

import jakarta.validation.constraints.NotBlank;

public record RelectureRequest(
        Integer note,
        @NotBlank(message = "le commentaire est obligatoire") String commentaire
) {
}
