package com.kfokam48.presence.repository;

import com.kfokam48.presence.domain.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    Optional<Presence> findBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    List<Presence> findByEtudiantId(Long etudiantId);

    long countBySessionId(Long sessionId);

    long countBySessionIdAndSource(Long sessionId, com.kfokam48.presence.domain.SourcePresence source);
}
