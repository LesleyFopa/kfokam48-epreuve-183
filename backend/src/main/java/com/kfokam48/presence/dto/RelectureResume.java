package com.kfokam48.presence.dto;

/** Une entrée par relecteur assigné (0 à 2, RG5), jamais son identité (RG7). */
public record RelectureResume(Integer note, String commentaire, String statut) {
}
