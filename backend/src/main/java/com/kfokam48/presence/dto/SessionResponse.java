package com.kfokam48.presence.dto;

import java.time.Instant;

public record SessionResponse(Long id, String code, Instant ouvertureAt, Instant expirationAt) {
}
