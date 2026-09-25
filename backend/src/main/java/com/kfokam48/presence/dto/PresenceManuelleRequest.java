package com.kfokam48.presence.dto;

import jakarta.validation.constraints.NotNull;

public record PresenceManuelleRequest(
        @NotNull(message = "l'étudiant est obligatoire") Long etudiantId
) {
}
