package com.kfokam48.presence.dto;

public record ExerciceVueEtudiant(
        Long id,
        Long sessionId,
        String lien,
        String statut,
        Integer note,
        String commentaire
) {
}
