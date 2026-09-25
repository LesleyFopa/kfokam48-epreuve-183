package com.kfokam48.presence.dto;

import java.util.List;

public record ExerciceVueEtudiant(
        Long id,
        Long sessionId,
        String lien,
        String statut,
        Double noteRetenue,
        boolean provisoire,
        List<RelectureResume> relectures
) {
}
