package com.kfokam48.presence.dto;

public record PresenceResponse(Long id, Long sessionId, Long etudiantId, String source) {
}
